package at.tuwien.aic2014.gr3.tweetsminer;

import at.tuwien.aic2014.gr3.shared.TweetRepository;
import org.apache.log4j.Logger;
import twitter4j.Status;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TweetsMiner implements Runnable {

    private static final Logger log = Logger.getLogger(TweetsMiner.class);

    private List<StatusProcessor> statusProcessors = new ArrayList<>();
    private TweetRepository tweetRepository;

    private boolean running = false;

    public void registerStatusProcessor (StatusProcessor statusProcessor) {
        this.statusProcessors.add(statusProcessor);
    }

    public void setTweetRepository(TweetRepository tweetRepository) {
        this.tweetRepository = tweetRepository;
    }

    public void setStatusProcessors(List<StatusProcessor> statusProcessors) {
        this.statusProcessors = statusProcessors;
    }

    @Override
    public void run() {
        log.debug("Starting mining unprocessed tweets...");

        running = true;
        Iterator<Status> it = tweetRepository.iterateTweetsWithUnprocessedUser();

        while (running && it.hasNext()) {
            Status status = it.next();

            log.debug("Processing tweet " + status.getId() + "...");

            for (StatusProcessor processor : statusProcessors) {
                processor.process(status);
            }

            log.debug("Tweet " + status.getId() + " successfully processed!");

            it.remove();
        }

        log.debug("Tweets mining process successfully shutdown! Remaining unprocessed tweets: " + it.hasNext());
    }

    public void shutdown() {
        log.debug("Shutting tweets mining process down...");
        running = false;
    }
}
