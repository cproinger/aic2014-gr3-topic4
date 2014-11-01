package at.tuwien.aic2014.gr3.shared;

import java.util.Iterator;

import twitter4j.Status;


public interface TweetRepository {

	void save(String rawJSON);

	/**
	 * call remove on the returned iterator to mark a tweet as 
	 * processed for users. 
	 * @return
	 * 		iterator for status where the user has not been processed yet. 
	 */
	Iterator<Status> iterateTweetsWithUnprocessedUser();

}