package at.tuwien.aic2014.gr3.twitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * mined die timeline eines users um die tweets
 * zwischen min/max tweetId zu bekommen. 
 * @author cproinger
 *
 */
@Service
public class TimelineApiMiner {

	private final static Logger LOG = LoggerFactory
			.getLogger(TimelineApiMiner.class);

	@Autowired
	private TwitterFactory twitterFactory;
	private Twitter twitter;

	public static void main(String[] args) {
		System.setProperty("twitter4j.jsonStoreEnabled", "true");
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
				TwitterConfig.class);
		TimelineApiMiner m = ctx.getBean(TimelineApiMiner.class);
		m.start();
		m.shutdown();
		ctx.close();
	}

	private void shutdown() {

	}

	private void start() {
		twitter = twitterFactory.getInstance();
		// ResponseList<User> usrs = twitter.lookupUsers(new long[] {123L});
		// for(User usr : usrs) {
		// //no tweets of user accessible
		// }
		long userId = 2857700128L;
		long sinceId = 529427289105510400L;
		long maxId = 531412688065544192L;
		int count = 200;// 20449;
		ResponseList<Status> timeline = null;
		int tweets = 0;
		try {
			do {
				LOG.info("maxId: " + maxId);
				timeline = twitter.getUserTimeline(userId, 
						new Paging(1, count, sinceId, maxId));
				LOG.info("timeline-size: " + timeline.size());
				tweets += timeline.size();
				if (timeline.size() > 0)
					maxId = timeline.get(timeline.size() -1).getId() - 1;
			} while (timeline.size() > 0);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		// hm 2255 also nicht alle 20499 was ist mit den anderen? 
		//scheinbar bekommt man maximal 3200
		//https://dev.twitter.com/rest/reference/get/statuses/user_timeline
		LOG.info("total: " + tweets);

		// 403 Your credentials do not allow access to this resource
		// LOG.info("contributees: " + twitter.getContributees(userId));
	}
}
