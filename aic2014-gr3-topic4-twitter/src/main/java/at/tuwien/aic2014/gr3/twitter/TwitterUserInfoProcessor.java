package at.tuwien.aic2014.gr3.twitter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.tuwien.aic2014.gr3.shared.TweetRepository;

@Service
public class TwitterUserInfoProcessor {

	@Autowired
	private TweetRepository tweetRepository;
	
	public void start() {
		
	}
}
