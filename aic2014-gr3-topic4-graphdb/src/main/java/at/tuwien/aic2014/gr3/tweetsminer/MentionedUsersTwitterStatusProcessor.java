package at.tuwien.aic2014.gr3.tweetsminer;

import at.tuwien.aic2014.gr3.graphdb.Neo4jTwitterUserRepository;
import at.tuwien.aic2014.gr3.domain.TwitterUser;
import at.tuwien.aic2014.gr3.shared.TwitterStatusProcessor;
import org.apache.log4j.Logger;
import twitter4j.Status;
import twitter4j.UserMentionEntity;

public class MentionedUsersTwitterStatusProcessor implements TwitterStatusProcessor {

    private static final Logger log = Logger.getLogger(MentionedUsersTwitterStatusProcessor.class);

    private Neo4jTwitterUserRepository twitterUserDao;

    public void setTwitterUserDao(Neo4jTwitterUserRepository twitterUserDao) {
        this.twitterUserDao = twitterUserDao;
    }

    @Override
    public void process(Status twitterStatus) {
        log.debug("Processing mentioned users from tweet...");

        TwitterUser tweetAuthor = new TwitterUser();
        tweetAuthor.setId(twitterStatus.getUser().getId());

        for (UserMentionEntity u : twitterStatus.getUserMentionEntities()) {
            TwitterUser mentionedUser = new TwitterUser();
            mentionedUser.setId(u.getId());

            log.debug("Found mentioned user: " + u);

            twitterUserDao.relation(tweetAuthor).mentioned(mentionedUser);
        }

        log.debug("Mentioned users successfully processed.");
    }
}
