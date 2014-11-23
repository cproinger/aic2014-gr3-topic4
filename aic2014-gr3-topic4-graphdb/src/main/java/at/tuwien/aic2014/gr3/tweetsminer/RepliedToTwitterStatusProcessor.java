package at.tuwien.aic2014.gr3.tweetsminer;

import at.tuwien.aic2014.gr3.dao.Neo4jTwitterUserDao;
import at.tuwien.aic2014.gr3.domain.TwitterUser;
import org.apache.log4j.Logger;
import twitter4j.Status;

public class RepliedToTwitterStatusProcessor implements TwitterStatusProcessor {

    private static final Logger log = Logger.getLogger(RepliedToTwitterStatusProcessor.class);

    private Neo4jTwitterUserDao twitterUserDao;

    public void setTwitterUserDao(Neo4jTwitterUserDao twitterUserDao) {
        this.twitterUserDao = twitterUserDao;
    }

    @Override
    public void process(Status twitterStatus) {
        log.debug("Processing replied to user from tweet...");

        if (twitterStatus.getInReplyToUserId() != -1) {
            log.debug("Replied to user found: " + twitterStatus.getInReplyToUserId());

            TwitterUser tweetAuthor = new TwitterUser();
            tweetAuthor.setId(twitterStatus.getUser().getId());

            TwitterUser repliedToUser = new TwitterUser();
            repliedToUser.setId(twitterStatus.getInReplyToUserId());

            twitterUserDao.user(tweetAuthor).repliedTo(repliedToUser);
        }

        log.debug("Replied to user successfully processed.");
    }
}
