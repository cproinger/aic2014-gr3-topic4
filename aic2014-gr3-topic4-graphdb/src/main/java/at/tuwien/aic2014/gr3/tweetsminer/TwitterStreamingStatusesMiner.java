package at.tuwien.aic2014.gr3.tweetsminer;

import at.tuwien.aic2014.gr3.shared.TweetRepository;
import at.tuwien.aic2014.gr3.shared.TwitterStatusProcessor;
import org.apache.log4j.Logger;
import twitter4j.Status;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TwitterStreamingStatusesMiner implements Runnable {

    private static final Logger log = Logger.getLogger(TwitterStreamingStatusesMiner.class);

    private TwitterStatusProcessor processor;
    private TweetRepository tweetRepository;

    private boolean running = false;

    public void setTweetRepository(TweetRepository tweetRepository) {
        this.tweetRepository = tweetRepository;
    }

    public void setProcessor(TwitterStatusProcessor processor) {
        this.processor = processor;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        log.info("Starting mining unprocessed tweets...");

        running = true;
        Iterator<Status> it = tweetRepository.iterateTweetsWithUnprocessedUser();

        while (running && it.hasNext()) {
            Status status;
            try {
                status = it.next();
            } catch (Exception e) {
                log.error("Exception thrown while fetching mongodb data!", e);
                continue;
            }

            if (isLanguageSupported(status)) {
                log.debug("Processing tweet " + status.getId() + "...");

                //possible performance improvements:
                //  * worker threads for statuses
                //  * distribution over multiple registered worker processes
                processor.process(status);

                log.debug("Tweet " + status.getId() + " successfully processed!");
            }
            else {
                log.debug("Skipping unsupported language tweet " + status.getId() + "...");
            }

            it.remove();
        }

        log.info("Tweets mining process successfully shutdown! Remaining unprocessed tweets: " + it.hasNext());
    }

    public void shutdown() {
        log.info("Shutting tweets mining process down...");
        running = false;
    }

    private boolean isLanguageSupported (Status inStatus) {
        return inStatus.getLang().equals("en");
    }
}
