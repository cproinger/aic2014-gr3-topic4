package at.tuwien.aic2014.gr3.shared;

import java.util.Iterator;

import at.tuwien.aic2014.gr3.domain.StatusRange;
import twitter4j.Status;


public interface TweetRepository {

	void save(String rawJSON);
	
	public long countTweets();

	/**
	 * call remove on the returned iterator to mark a tweet as 
	 * processed for users. 
	 * @return
	 * 		iterator for status where the user has not been processed yet. 
	 */
	Iterator<Status> iterateTweetsWithUnprocessedUser();

	Iterator<StatusRange> iterateStatusRanges();

}
