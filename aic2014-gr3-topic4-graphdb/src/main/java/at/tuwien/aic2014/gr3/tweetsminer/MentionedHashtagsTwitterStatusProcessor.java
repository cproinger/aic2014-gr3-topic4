package at.tuwien.aic2014.gr3.tweetsminer;

import at.tuwien.aic2014.gr3.dao.Neo4jTwitterUserDao;
import at.tuwien.aic2014.gr3.domain.TwitterUser;
import at.tuwien.aic2014.gr3.shared.TwitterStatusProcessor;
import org.apache.log4j.Logger;
import twitter4j.HashtagEntity;
import twitter4j.Status;

public class MentionedHashtagsTwitterStatusProcessor implements TwitterStatusProcessor {

    private static final Logger log = Logger.getLogger(MentionedHashtagsTwitterStatusProcessor.class);

    private Neo4jTwitterUserDao twitterUserDao;

    public void setTwitterUserDao(Neo4jTwitterUserDao twitterUserDao) {
        this.twitterUserDao = twitterUserDao;
    }

    @Override
    public void process(Status twitterStatus) {
        log.debug("Processing mentioned hashtags from tweet...");

        TwitterUser tweetAuthor = new TwitterUser();
        tweetAuthor.setId(twitterStatus.getUser().getId());

        for (HashtagEntity hashtag : twitterStatus.getHashtagEntities()) {
            log.debug("Found mentioned hashtag: " + hashtag);

            twitterUserDao.user(tweetAuthor).mentionedHashtag(hashtag.getText());
        }

        log.debug("Mentioned hashtags successfully processed.");
    }
}
