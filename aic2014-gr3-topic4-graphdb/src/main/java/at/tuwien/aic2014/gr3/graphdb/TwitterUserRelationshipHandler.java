package at.tuwien.aic2014.gr3.graphdb;

import at.tuwien.aic2014.gr3.domain.TwitterUser;

public interface TwitterUserRelationshipHandler {

    public void mentioned(TwitterUser twitterUser);
    public void retweeted(TwitterUser twitterUser);
    public void isFriendOf(TwitterUser twitterUser);
    public void repliedTo(TwitterUser twitterUser);
    public void mentionedHashtag(String hashtag);
    public void mentionedTopic(String topic);
}
