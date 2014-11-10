package at.tuwien.aic2014.gr3.twitter;

import java.io.IOException;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import at.tuwien.aic2014.gr3.shared.TweetRepository;

/**
 * @author cproinger
 *
 */
@Service
public class StreamApiMiner {
	
	private StatusListener listener = new StatusListener() {
		public void onStatus(Status status) {
			
			//nur englische tweets speichern. (deutsche gibts zu wenige)
			if(!"en".equals(status.getLang()))
				return;
			
			if(missingCount(status)) {
				return;
			}
			//https://dev.twitter.com/overview/api/tweets
			System.out.println(status.getText());
			String rawJSON = TwitterObjectFactory.getRawJSON(status);
			//TODO: da doppelte nachrichten kommen können sollten
			//wir einen index auf die tweet-id legen (und ev. write-concern umstellen). 
			tweetRepository.save(rawJSON);
			
//			try {
//				ResponseList<Status> l = TwitterFactory.getSingleton().lookup(new long[]{status.getId()});
//				for(Status s : l) {
//					s.getRetweetedStatus()
//				}
//			} catch (TwitterException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}

		private boolean missingCount(Status status) {
			//in rare cases this field will be omitted by sending -1
			//https://dev.twitter.com/streaming/overview/processing
			User user = status.getUser();
			if(user.getFavouritesCount() == -1)
				return true;
			if(user.getFollowersCount() == -1)
				return true;
			if(user.getFriendsCount() == -1)
				return true;
			return false;
		}

		public void onDeletionNotice(
				StatusDeletionNotice statusDeletionNotice) {
		}

		public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
		}

		public void onException(Exception ex) {
			ex.printStackTrace();
		}

		@Override
		public void onScrubGeo(long arg0, long arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStallWarning(StallWarning arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	private TwitterStream twitterStream;
	
	@Autowired
	private TwitterStreamFactory twitterStreamFactory;
	
//	@Autowired
//	private Twitter twitter;
	
	@Autowired
	private TweetRepository tweetRepository;

	public void start() throws TwitterException, IOException {
		twitterStream = twitterStreamFactory.getInstance();
		twitterStream.addListener(listener);
//		String[] track = new String[] { "car" };
//		String[] language = new String[] { "de", "en" };
//		twitterStream.filter(new FilterQuery()
//					.track(track )// man muss das angeben, schränkt allerdings die tweets stark ein
//					.language(language)
//		);
		// sample() method internally creates a thread which manipulates
		// TwitterStream and calls these adequate listener methods continuously.
		twitterStream.sample();
	}
	
	@PreDestroy
	public void shutdown() {
		//twitterStream.cleanUp();
		twitterStream.shutdown();
	}
	
	public static void main(String[] args) {
		System.setProperty("twitter4j.jsonStoreEnabled", "true");
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(TwitterConfig.class);
		StreamApiMiner m = ctx.getBean(StreamApiMiner.class);
		try {
			m.start();
		} catch (TwitterException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(2 * 60_000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		m.shutdown();
		ctx.close();
	}
	
	
	// statuses_max - statuses_min => wieviele tweets hat der user in dem zeitraum gemacht
	//   tweets müssen aber nicht in db sein. 
	/*
db.streamingTweets.aggregate([
    {$match : {  "user.followers_count" : { $gt : 0} }}, 
{ $group : { _id : "$user.id", followers : {$max : "$user.followers_count"}, 
    friends : {$max : "$user.friends_count"}, 
    statuses_max : {$max : "$user.statuses_count"},
    statuses_min : {$min : "$user.statuses_count"} }},
{ $match : { "followers" : { $gt : 0 }, "statuses_min" : {$lt : "$statuses_max"} }}
])
	 */
	
	//wieviele tweets sind in der db, und wieviele tweets hat der 
	//user in dem zeitraum erstellt
	/*
db.streamingTweets.aggregate([
{ $match : { "user.statuses_count" : {$gt : 0}} },
{ $group : { _id : "$user.id", 
    anz : {$sum : 1}, 
    statuses_max : {$max : "$user.statuses_count"},
    statuses_min : {$min : "$user.statuses_count"} }}, 
{ $project : { tweetrange : {$subtract : ["$statuses_max" , "$statuses_min"]}
    , anz : "$anz", 
statuses_min : "$statuses_min", statuses_max : "$statuses_max"}}, 
{ $match : { tweetrange : { $gt : 0 } }}, 
{ $sort : {tweetrange : -1}}
])
	 */
	
	//tweet-anzahl mit usernamen
	/*
db.streamingTweets.aggregate([
{ $match : { "user.statuses_count" : {$gt : 0}, "user.followers_count" : {$gt : 500} }},
{ $group : { _id : "$user.id", uname : {$max : "$user.screen_name"}, name : {$max : "$user.name"},
    anz : {$sum : 1}, 
    statuses_max : {$max : "$user.statuses_count"},
    statuses_min : {$min : "$user.statuses_count"} }}, 
{ $project : { tweetrange : {$subtract : ["$statuses_max" , "$statuses_min"]}
    , anz : "$anz", uname : "$uname", name : "$name",
statuses_min : "$statuses_min", statuses_max : "$statuses_max"}}, 
{ $match : { tweetrange : { $gt : 0 } }}, 
{ $sort : {tweetrange : -1}}
])
	 */
}
