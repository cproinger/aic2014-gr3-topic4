package at.tuwien.aic2014.gr3.tweetsminer;

import at.tuwien.aic2014.gr3.domain.TwitterUser;
import at.tuwien.aic2014.gr3.shared.RepositoryException;
import at.tuwien.aic2014.gr3.shared.TwitterStatusProcessor;
import at.tuwien.aic2014.gr3.shared.TwitterUserRepository;
import org.apache.log4j.Logger;
import twitter4j.Status;

public class ProcessedStatusesCountStatusProcessor implements TwitterStatusProcessor {

    private static final Logger log = Logger.getLogger(ProcessedStatusesCountStatusProcessor.class);

    private TwitterUserRepository twitterUserRepository;

    public void setTwitterUserRepository(TwitterUserRepository twitterUserRepository) {
        this.twitterUserRepository = twitterUserRepository;
    }

    @Override
    public void process(Status twitterStatus) {
        log.debug("Processing total number of statuses from user...");

        try {
            TwitterUser user = twitterUserRepository.readById(twitterStatus.getUser().getId());

            if (user == null) {
                log.debug("First time status from user " + twitterStatus.getUser().getId());
                user = new TwitterUser();
                user.setId(twitterStatus.getUser().getId());
                user.setProcessedStatusesCount(1);
            }
            else {
                log.debug("Already processed " + user.getProcessedStatusesCount() + " from user " + user.getId());
                user.setProcessedStatusesCount(user.getProcessedStatusesCount() + 1);
            }

            twitterUserRepository.save(user);
        } catch (RepositoryException e) {
            log.error("Exception caught from repo layer", e);
        }
    }
}
