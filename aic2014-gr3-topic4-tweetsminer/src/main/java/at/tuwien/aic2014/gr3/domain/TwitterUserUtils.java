package at.tuwien.aic2014.gr3.domain;

import twitter4j.User;

public class TwitterUserUtils {

    public TwitterUser create(User fromUser) {
        TwitterUser twitterUser = new TwitterUser();

        twitterUser.setId(-1);
        twitterUser.setName(fromUser.getName());
        twitterUser.setScreenName(fromUser.getScreenName());
        twitterUser.setLocation(fromUser.getLocation());
        twitterUser.setUrl(fromUser.getURL());
        twitterUser.setDescription(fromUser.getDescription());
        twitterUser.setUserProtected(fromUser.isProtected());
        twitterUser.setUserVerified(fromUser.isVerified());
        twitterUser.setFollowersCount(fromUser.getFollowersCount());
        twitterUser.setFriendsCount(fromUser.getFriendsCount());
        twitterUser.setListedCount(fromUser.getListedCount());
        twitterUser.setFavouritesCount(fromUser.getFavouritesCount());
        twitterUser.setCreatedAt(fromUser.getCreatedAt());
        twitterUser.setLanguage(fromUser.getLang());
        twitterUser.setLastTimeSynched(null);

        return twitterUser;
    }
}
