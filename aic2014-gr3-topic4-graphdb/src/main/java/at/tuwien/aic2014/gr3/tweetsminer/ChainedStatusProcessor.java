package at.tuwien.aic2014.gr3.tweetsminer;

import at.tuwien.aic2014.gr3.shared.TwitterStatusProcessor;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import twitter4j.Status;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChainedStatusProcessor implements TwitterStatusProcessor {

    private static final Logger log = Logger.getLogger(ChainedStatusProcessor.class);

    private List<TwitterStatusProcessor> twitterStatusProcessors = new ArrayList<>();

    public void setTwitterStatusProcessors(List<TwitterStatusProcessor> twitterStatusProcessors) {
        this.twitterStatusProcessors = twitterStatusProcessors;
    }

    @Override
    @Transactional
    public void process(Status twitterStatus) {
        log.debug("Chained status processor processing twitter status...");

        if (twitterStatus.getUser() == null) {
            log.warn ("Found tweet with no user...");
            return;
        }
        if (!isLanguageSupported(twitterStatus)) {
            log.warn ("Unsupported language for tweet");
            return;
        }

        for (TwitterStatusProcessor processor : twitterStatusProcessors) {
            try {
                processor.process(twitterStatus);
            }
            catch (Exception e) {
                log.warn ("Exception thrown during tweet process step!", e);
            }
        }

        log.debug("Chained status processor successfully processed twitter status");
    }

    private boolean isLanguageSupported(Status twitterStatus) {
        return twitterStatus.getLang().equals("en");
    }
}
