package at.tuwien.aic2014.gr3.tweetsminer;

import at.tuwien.aic2014.gr3.shared.TweetRepository;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import twitter4j.Status;
import twitter4j.TwitterObjectFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TweetsMiner implements Runnable {

    private static final Logger log = Logger.getLogger(TweetsMiner.class);

    private List<TweetProcessor> tweetProcessors = new ArrayList<>();
    private TweetRepository tweetRepository;

    private boolean running = false;

    public void setTweetRepository(TweetRepository tweetRepository) {
        this.tweetRepository = tweetRepository;
    }

    public void setTweetProcessors(List<TweetProcessor> tweetProcessors) {
        this.tweetProcessors = tweetProcessors;
    }

    @Override
    public void run() {
        log.debug("Starting mining unprocessed tweets...");

        running = true;
        Iterator<Status> it = tweetRepository.iterateTweetsWithUnprocessedUser();

        while (running && it.hasNext()) {
            Status status = it.next();

            if (isLanguageSupported(status)) {
                log.debug("Processing tweet " + status.getId() + "...");

                for (TweetProcessor processor : tweetProcessors) {
                    try {
                        processor.process(new JSONObject(TwitterObjectFactory.getRawJSON(status)));
                    }
                    catch (JSONException e) {
                        log.warn ("Exception thrown during tweet process step: " + e.getMessage());
                    }
                }

                log.debug("Tweet " + status.getId() + " successfully processed!");
            }
            else {
                log.debug("Skipping unsupported language tweet " + status.getId() + "...");
            }

            it.remove();
        }

        log.debug("Tweets mining process successfully shutdown! Remaining unprocessed tweets: " + it.hasNext());
    }

    public void shutdown() {
        log.debug("Shutting tweets mining process down...");
        running = false;
    }

    private boolean isLanguageSupported (Status inStatus) {
        return inStatus.getLang().equals("en");
    }
}
