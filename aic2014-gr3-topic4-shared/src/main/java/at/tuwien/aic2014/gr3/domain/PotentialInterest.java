package at.tuwien.aic2014.gr3.domain;

public class PotentialInterest implements IHasTopic {

	/**
	 * the user this interest-topic is from . 
	 */
	private TwitterUser twitterUser;
	
	/**
	 * gibt an wie lange der pfad bis zu diesem user ist
	 */
	private int len;
	
	private String topic;

	
	
	public PotentialInterest(TwitterUser twitterUser, int len, String topic) {
		super();
		this.twitterUser = twitterUser;
		this.len = len;
		this.topic = topic;
	}

	public TwitterUser getTwitterUser() {
		return twitterUser;
	}

	public int getLen() {
		return len;
	}

	public String getTopic() {
		return topic;
	}


	@Override
	public String toString() {
		return "PotentialInterest [twitterUser=" + twitterUser + ", len=" + len
				+ ", topic=" + topic + "]";
	}
	
	
}
