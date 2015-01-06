package at.tuwien.aic2014.gr3.domain;
/**
 * value-object for user and count. 
 */
public class UserRetweetedCount {
	private TwitterUser user;
	private int count;
	public UserRetweetedCount(TwitterUser user, int count) {
		super();
		this.user = user;
		this.count = count;
	}
	@Override
	public String toString() {
		return "UserRetweetedCount [user=" + user + ", count=" + count
				+ "]";
	}
	
	public TwitterUser getUser() {
		return user;
	}
	public int getCount() {
		return count;
	}
}
