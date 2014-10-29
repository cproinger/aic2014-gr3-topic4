package at.tuwien.aic2014.gr3.twitter;

import java.io.IOException;

import twitter4j.FilterQuery;
import twitter4j.ResponseList;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

/**
 * @author cproinger
 *
 */
public class Miner {
	
	private StatusListener listener = new StatusListener() {
		public void onStatus(Status status) {
			System.out.println(status + ": " + status.getRetweetedStatus());
			
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

	public void start() throws TwitterException, IOException {
		
		twitterStream = new TwitterStreamFactory().getInstance();
		twitterStream.addListener(listener);
		String[] track = new String[] { "car" };
		String[] language = new String[] { "de" };
		twitterStream.filter(new FilterQuery().track(track ).language(language));
		// sample() method internally creates a thread which manipulates
		// TwitterStream and calls these adequate listener methods continuously.
		twitterStream.sample();
	}
	
	public void shutdown() {
		//twitterStream.cleanUp();
		twitterStream.shutdown();
	}
	
	public static void main(String[] args) {
		Miner m = new Miner();
		try {
			m.start();
		} catch (TwitterException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(30_000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		m.shutdown();
	}
}
