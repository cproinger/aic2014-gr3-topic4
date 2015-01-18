package at.tuwien.aic2014.gr3.docstore;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import com.mongodb.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import at.tuwien.aic2014.gr3.domain.StatusRange;
import at.tuwien.aic2014.gr3.shared.TweetRepository;

import com.mongodb.util.JSON;

@Repository
public class MongoTweetRepository implements TweetRepository {
	
	private static final String ID = "id";

	private static final String USER = "user";

	private static final String TWEET_ID = "tweetId";

	private static final String AIC_PROCESSED_USER = "aic_processed_user";

	private static final class MarkProcessedStatusIterator implements Iterator<Status> {
		private final DBCursor tweetsWithUnprocressedUser;
		private DBObject dbo;
		private String processedField;

		private MarkProcessedStatusIterator(DBCursor tweetsWithUnprocressedUser
				, String processedField) {
			this.tweetsWithUnprocressedUser = tweetsWithUnprocressedUser;
			this.processedField = processedField;
		}

		@Override
		public Status next() {
			dbo = tweetsWithUnprocressedUser.next();
			try {
				return TwitterObjectFactory.createStatus(dbo.toString());
			} catch (TwitterException e) {
				LOG.error("error parsing status-json-string", e);
				throw new RuntimeException("unparseable json string");
			}
		}

		@Override
		public void remove() {
			if(dbo == null)
				throw new IllegalStateException("next has not been called yet");
			dbo.put(processedField, new Date());
			tweetsWithUnprocressedUser.getCollection().save(dbo);
			dbo = null;
		}

		@Override
		public boolean hasNext() {
			return tweetsWithUnprocressedUser.hasNext();
		}
	}

	private final static Logger LOG = LoggerFactory.getLogger(MongoTweetRepository.class);

	@Autowired
	private DB db;

	private boolean streamingTweetsIndexesEnsured;

	private boolean userTweetsIndexesEnsured;
	
	@Override
	public long countTweets() {
		return getStreamingTweetsCollection().count();
	}

    public void setDb(DB db) {
        this.db = db;
    }

    /**
	 * @param json
	 * 
	 */
	@Override
	public void save(String json) {
		// http://stackoverflow.com/a/22177940/775513: says that this will not
		// handle dates correctly
		// but it's a start
		DBObject obj = (DBObject) JSON.parse(json);
		DBObject retweetedStatus = (DBObject) obj.get("retweeted_status");
		Object tweetId = obj.get(ID);
		//upsert tweet. 
		getStreamingTweetsCollection().update(new BasicDBObject(ID, tweetId), obj, true, false);
		DBObject tweetUser = (DBObject) obj.get(USER);
		tweetUser.put("original", retweetedStatus == null);
		tweetUser.put(TWEET_ID, tweetId);
		//upsert user-tweet: aber nur wenn sich der status_count nicht geändert hat
		//sonst verlieren wir bei der nächsten iteration über die status-ranges
		//tweets
		getUserTweetsCollection().update(new BasicDBObject(ID, tweetUser.get(ID))
												.append(TWEET_ID, tweetId)
												.append("statuses_count", tweetUser.get("statuses_count")),
												tweetUser, true, false);
		if(retweetedStatus != null) {
			//upsert retweeted-user-tweet. 
			DBObject retweetedUser = (DBObject) retweetedStatus.get(USER);
			retweetedUser.put("original", 1);
			Object retweetedId = retweetedStatus.get(ID);
			retweetedUser.put(TWEET_ID, retweetedId);
			//upsert user-tweet: aber nur wenn sich der status_count nicht geändert hat
			//sonst verlieren wir bei der nächsten iteration über die status-ranges
			//tweets
			getUserTweetsCollection().update(new BasicDBObject(ID, retweetedUser.get(ID))
											.append(TWEET_ID, retweetedId)
											.append("statuses_count", tweetUser.get("statuses_count")),
											retweetedUser, true, false);
		}
	}

	private DBCollection getStreamingTweetsCollection() {
		DBCollection streamingTweets = db.getCollection("streamingTweets");
		if(!streamingTweetsIndexesEnsured) {
			streamingTweets.createIndex(new BasicDBObject(ID, 1));
			streamingTweets.createIndex(new BasicDBObject("user.id", 1));
			streamingTweetsIndexesEnsured = true;
		}
		return streamingTweets;
	}

	@Override
	public Iterator<Status> iterateTweetsWithUnprocessedUser() {
		final DBCursor tweetsWithUnprocressedUser = getStreamingTweetsCollection().find(
				new BasicDBObject(AIC_PROCESSED_USER, new BasicDBObject("$exists",
						false)))
                .addOption(Bytes.QUERYOPTION_NOTIMEOUT);
		LOG.info("tweetsWithUnprocressedUser: " + tweetsWithUnprocressedUser.count());
		
		return new MarkProcessedStatusIterator(tweetsWithUnprocressedUser, AIC_PROCESSED_USER);
	}
	
	@Override
	public Iterator<StatusRange> iterateStatusRanges() {
		DBObject group = new BasicDBObject("$group", new BasicDBObject("_id", "$id")
				.append("min_tweet", new BasicDBObject("$min", "$tweetId"))
				.append("max_tweet", new BasicDBObject("$max", "$tweetId"))
				.append("min_status", new BasicDBObject("$min", "$statuses_count"))
				.append("max_status", new BasicDBObject("$max", "$statuses_count"))
				.append("original", new BasicDBObject("$sum", "$original")));
		
		BasicDBList maxMinusMin = new BasicDBList();
		maxMinusMin.add("$max_status");
		maxMinusMin.add("$min_status");
		DBObject calcStatusRange = new BasicDBObject("$project", new BasicDBObject()
				.append("status_range", new BasicDBObject("$subtract", maxMinusMin))
				.append("original", 1)
				.append("max_status", 1)
				.append("min_status", 1)
				.append("max_tweet", 1)
				.append("min_tweet", 1));
		DBObject matchMoreThanOneStatus = new BasicDBObject("$match", new BasicDBObject("status_range", new BasicDBObject("$gt", 0)));
		
		DBObject sortByOriginalAndStatusRange = new BasicDBObject("$sort", new BasicDBObject()
			.append("orginal", -1)
			.append("status_range", -1));
		
		AggregationOutput stats = getUserTweetsCollection().aggregate(
				Arrays.asList(group, calcStatusRange, matchMoreThanOneStatus, sortByOriginalAndStatusRange));
		Iterator<DBObject> dbos = stats.results().iterator();
		return new Iterator<StatusRange>() {

			private DBObject current;

			@Override
			public boolean hasNext() {
				//TODO try to lookup range to check if it was already processed.
				boolean exists = false;
				do {
					if(!dbos.hasNext())
						return false;
					current = dbos.next();
					BasicDBObject q = new BasicDBObject("userId", current.get("_id"))
											.append("min_tweet", current.get("min_tweet"))
											.append("max_tweet", current.get("max_tweet"));
					exists = (null != getUserTweetRangeCollection().findOne(q));
				} while(exists);
				return current != null;
			}

			@Override
			public StatusRange next() {
				if(current == null)
					throw new IllegalStateException("check hasNext() first");
				DBObject o = current;
				long min = ((Number) o.get("min_tweet")).longValue();
				long max = ((Number) o.get("max_tweet")).longValue();
				//niedrigere long-werte werden von JSON.parse in int übersetzt und sind dann
				//so in der DB. also muss man sicherheitshalber so auf den long-wert kommen. 
				long userId = ((Number) o.get("_id")).longValue();
				return new StatusRange(userId, min, max);
			}
			
			@Override
			public void remove() {
				if(current == null)
					throw new IllegalStateException("check hasNext() first");
				BasicDBObject o = new BasicDBObject();
				o.append("userId", current.get("_id"));
				o.append("min_tweet", current.get("min_tweet"));
				o.append("max_tweet", current.get("max_tweet"));
				getUserTweetRangeCollection().save(o);
			}

			private DBCollection getUserTweetRangeCollection() {
				return db.getCollection("user_tweet_ranges");
			}
		};
	}

	private DBCollection getUserTweetsCollection() {
		DBCollection userTweets = db.getCollection("user_tweets");
		if(!userTweetsIndexesEnsured) {
			userTweets.createIndex(new BasicDBObject(ID, 1));
			userTweets.createIndex(new BasicDBObject(ID, 1).append(TWEET_ID, 1));
		}
		return userTweets;
	}
}
