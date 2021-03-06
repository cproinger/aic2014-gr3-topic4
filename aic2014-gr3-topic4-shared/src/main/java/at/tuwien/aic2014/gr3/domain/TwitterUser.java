package at.tuwien.aic2014.gr3.domain;

import java.util.Date;

public class TwitterUser {

    private long id = -1;
    private String name;
    private String screenName;
    private String location;
    private String url;
    private String description;
    private boolean userProtected;
    private boolean userVerified;
    private int followersCount;
    private int friendsCount;
    private int listedCount;
    private int favouritesCount;
    private int statusesCount;
    private Date createdAt;
    private String language;
    private Date lastTimeSynched;
    private boolean isProtected;
    private boolean isVerified;
    private int processedStatusesCount;

    @Override
    public int hashCode() {
        return (int) id + followersCount + friendsCount + favouritesCount + statusesCount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || ! (obj instanceof TwitterUser)) {
            return false;
        }

        return this.id == ((TwitterUser) obj).id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getStatusesCount() {
        return statusesCount;
    }

    public void setStatusesCount(int statusesCount) {
        this.statusesCount = statusesCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isUserProtected() {
        return userProtected;
    }

    public void setUserProtected(boolean userProtected) {
        this.userProtected = userProtected;
    }

    public boolean isUserVerified() {
        return userVerified;
    }

    public void setUserVerified(boolean userVerified) {
        this.userVerified = userVerified;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }

    public int getListedCount() {
        return listedCount;
    }

    public void setListedCount(int listedCount) {
        this.listedCount = listedCount;
    }

    public int getFavouritesCount() {
        return favouritesCount;
    }

    public void setFavouritesCount(int favouritesCount) {
        this.favouritesCount = favouritesCount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Date getLastTimeSynched() {
        return lastTimeSynched;
    }

    public void setLastTimeSynched(Date lastTimeSynched) {
        this.lastTimeSynched = lastTimeSynched;
    }

    public boolean getIsProtected(){return isProtected;}

    public void setIsProtected(boolean isProtected) {
        this.isProtected = isProtected;
    }

    public boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    public int getProcessedStatusesCount() {
        return processedStatusesCount;
    }

    public void setProcessedStatusesCount(int processedStatusesCount) {
        this.processedStatusesCount = processedStatusesCount;
    }

	@Override
	public String toString() {
		return "TwitterUser [id=" + id + ", name=" + name + "]";
	}
}
