package at.tuwien.aic2014.gr3.tweetsminer;

import at.tuwien.aic2014.gr3.dao.Neo4jTwitterUserDao;
import at.tuwien.aic2014.gr3.domain.TwitterUser;
import at.tuwien.aic2014.gr3.shared.TwitterStatusProcessor;
import org.apache.log4j.Logger;
import twitter4j.Status;

public class RetweetedUserTwitterStatusProcessor implements TwitterStatusProcessor {

    private static final Logger log = Logger.getLogger(RetweetedUserTwitterStatusProcessor.class);

    private Neo4jTwitterUserDao twitterUserDao;

    public void setTwitterUserDao(Neo4jTwitterUserDao twitterUserDao) {
        this.twitterUserDao = twitterUserDao;
    }

    @Override
    public void process(Status twitterStatus) {
        log.debug("Processing retweeted user from tweet...");

        if (twitterStatus.getRetweetedStatus() != null) {
            log.debug("Retweeted user found: " + twitterStatus.getRetweetedStatus().getUser());

            TwitterUser tweetAuthor = new TwitterUser();
            tweetAuthor.setId(twitterStatus.getUser().getId());

            TwitterUser retweetedUser = new TwitterUser();
            retweetedUser.setId(twitterStatus.getRetweetedStatus().getUser().getId());

            twitterUserDao.user(tweetAuthor).retweeted(retweetedUser);
        }

        log.debug("Retweeted user successfully processed.");
    }
}
