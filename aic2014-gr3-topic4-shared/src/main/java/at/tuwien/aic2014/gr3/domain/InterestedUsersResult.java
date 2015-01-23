package at.tuwien.aic2014.gr3.domain;

public class InterestedUsersResult {

	private TwitterUser user;
	private double skew;
	
	public InterestedUsersResult(TwitterUser user, double skew) {
		super();
		this.user = user;
		this.skew = skew;
	}
	
	public TwitterUser getUser() {
		return user;
	}
	public double getSkew() {
		return skew;
	}
	
	
}
