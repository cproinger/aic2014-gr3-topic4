package at.tuwien.aic2014.gr3.twitter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import at.tuwien.aic2014.gr3.shared.TweetRepository;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

@Service
public class DatasetMiner {
	
	private final static Logger LOG = LoggerFactory.getLogger(DatasetMiner.class);

	@Autowired
	private TimelineApiMiner timelineApiMiner;
	
	@Autowired
	private RelationshipMiner relationshipsMiner;
	
	@Autowired
	private StreamApiMiner streamMiner;
	
	@Autowired 
	private TwitterFactory twitterFactory;

	
	@Autowired
	private TweetRepository tweetRepository;

	public static void main(String[] args) throws TwitterException {
		System.setProperty("twitter4j.jsonStoreEnabled", "true");
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
				TwitterConfig.class);
		DatasetMiner m = ctx.getBean(DatasetMiner.class);
		m.start();
		ctx.close();
	}
	

	void start() throws TwitterException {
		//mine tweets until there are min 100.000 in the db. 
		while(tweetRepository.countTweets() < 100000L) {
			int persisted = timelineApiMiner.mineUserTimelines();
			if(persisted == 0) {
				try {
					streamMiner.start();
					try {
						Thread.sleep(60 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					streamMiner.shutdown();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					LOG.info("waiting for 1 minute(s)");
					Thread.sleep(1 * 60_000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
