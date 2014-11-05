package at.tuwien.aic2014.gr3.tweetsminer;

import at.tuwien.aic2014.gr3.dao.Neo4jTwitterUserDao;
import at.tuwien.aic2014.gr3.domain.TwitterUser;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class MentionedUsersTweetProcessor implements TweetProcessor {

    private static final Logger log = Logger.getLogger(MentionedUsersTweetProcessor.class);

    private Neo4jTwitterUserDao twitterUserDao;

    public void setTwitterUserDao(Neo4jTwitterUserDao twitterUserDao) {
        this.twitterUserDao = twitterUserDao;
    }

    @Override
    public void process(JSONObject tweetJson) {
        log.debug("Processing mentioned users from tweet...");

        TwitterUser tweetAuthor = new TwitterUser();
        tweetAuthor.setId(tweetJson.getJSONObject(USER_ENTRY).getLong(USER_ID_ENTRY));

        JSONArray mentionedUsers = tweetJson.getJSONObject(ENTITIES_ENTRY).getJSONArray(USER_MENTIONS_ENTRY);

        for (int i = 0; i < mentionedUsers.length(); ++i) {
            TwitterUser mentionedUser = new TwitterUser();
            mentionedUser.setId(mentionedUsers.getJSONObject(i).getLong(USER_ID_ENTRY));

            log.debug("Found mentioned user: " + mentionedUsers.getJSONObject(i));

            twitterUserDao.user(tweetAuthor).mentioned(mentionedUser);
        }

        log.debug("Mentioned users successfully processed.");
    }
}
