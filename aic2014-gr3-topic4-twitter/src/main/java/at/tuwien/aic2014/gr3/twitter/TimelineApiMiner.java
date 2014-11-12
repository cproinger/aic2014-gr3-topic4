package at.tuwien.aic2014.gr3.twitter;

import java.util.Iterator;

import org.neo4j.unsafe.impl.batchimport.cache.NextFieldManipulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import at.tuwien.aic2014.gr3.domain.StatusRange;
import at.tuwien.aic2014.gr3.shared.TweetRepository;
import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterObjectFactory;

/**
 * mined die timeline eines users um die tweets zwischen min/max tweetId zu
 * bekommen.
 * 
 * @author cproinger
 *
 */
@Service
public class TimelineApiMiner {

	private static final int PAGE_SIZE = 200;

	private final static Logger LOG = LoggerFactory
			.getLogger(TimelineApiMiner.class);

	@Autowired
	private TweetRepository tweetRepository;

	@Autowired
	private TwitterFactory twitterFactory;

	private Twitter twitter;

	public static void main(String[] args) throws TwitterException {
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

	private void start() throws TwitterException {
		twitter = twitterFactory.getInstance();
		// ResponseList<User> usrs = twitter.lookupUsers(new long[] {123L});
		// for(User usr : usrs) {
		// //no tweets of user accessible
		// }
		for (Iterator<StatusRange> it = tweetRepository.iterateStatusRanges(); it
				.hasNext();) {
			StatusRange range = it.next();
			LOG.info(range + "");
			// 20449;
			ResponseList<Status> timeline = null;
			int tweets = 0;
			long maxId = range.getToStatusId();
			do {
				LOG.info("maxId: " + maxId);
				timeline = getTimeline(range, maxId);
				tweets += timeline.size();
				if (timeline.size() > 0)
					maxId = timeline.get(timeline.size() - 1).getId() - 1;
				for (Iterator<Status> statIt = timeline.iterator(); statIt
						.hasNext();) {
					String rawJSON = TwitterObjectFactory.getRawJSON(statIt
							.next());
					tweetRepository.save(rawJSON);
				}
			} while (timeline.size() > 0);
			LOG.info("total: " + tweets);
		}
		// hm 2255 also nicht alle 20499 was ist mit den anderen?
		// scheinbar bekommt man maximal 3200
		// https://dev.twitter.com/rest/reference/get/statuses/user_timeline

		// 403 Your credentials do not allow access to this resource
		// LOG.info("contributees: " + twitter.getContributees(userId));
	}

	private ResponseList<Status> getTimeline(StatusRange range, long maxId) throws TwitterException {
		ResponseList<Status> timeline = null;
		boolean success = false;
		int tries = 0;
		do {			
			try {
				timeline = twitter.getUserTimeline(range.getUserId(), new Paging(1,
						PAGE_SIZE, range.getFromStatusId(), maxId));
				success = true;
			} catch (TwitterException e) {
				tries++;
				if(e.getErrorCode() == 88) {
					RateLimitStatus rts = e.getRateLimitStatus();
					
					try {
						//back off
						int i = rts.getSecondsUntilReset();
						LOG.info("rate-limited: starting try nr: " + (tries+1) + " in " + (i) + " seconds");
						Thread.sleep(i * 1000);
					} catch (InterruptedException e1) {
						throw new RuntimeException("unexpected interrupt");
					}
				} else {
//					LOG.error("twitter-exception", e);
					throw e;
				}
			}
		} while(!success);
		LOG.info("timeline-size: " + timeline.size());
		return timeline;
	}
}
