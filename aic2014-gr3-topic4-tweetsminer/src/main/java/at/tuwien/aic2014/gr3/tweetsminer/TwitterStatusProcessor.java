package at.tuwien.aic2014.gr3.tweetsminer;

import twitter4j.Status;

public interface TwitterStatusProcessor {

    public static final String USER_ENTRY = "user";
    public static final String USER_ID_ENTRY = "id";

    public static final String ENTITIES_ENTRY = "entities";
    public static final String USER_MENTIONS_ENTRY = "user_mentions";

    public void process(Status twitterStatus);
}
