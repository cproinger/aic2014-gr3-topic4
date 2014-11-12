package at.tuwien.aic2014.gr3.docstore;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import at.tuwien.aic2014.gr3.domain.StatusRange;
import at.tuwien.aic2014.gr3.shared.TweetRepository;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

@Repository
public class MongoTweetRepository implements TweetRepository {
	
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
		//upsert. 
		getCollection().update(new BasicDBObject("id", obj.get("id")), obj, true, false);
		DBObject tweetUser = (DBObject) obj.get("user");
		tweetUser.put("original", 0);
		tweetUser.put("tweetId", obj.get("id"));
		getUserTweetsCollection().save(tweetUser);
		DBObject retweetedStatus = (DBObject) obj.get("retweeted_status");
		if(retweetedStatus != null) {
			DBObject retweetedUser = (DBObject) retweetedStatus.get("user");
			retweetedUser.put("original", 1);
			retweetedUser.put("tweetId", retweetedStatus.get("id"));
			getUserTweetsCollection().save(retweetedUser);
		}
	}

	private DBCollection getCollection() {
		DBCollection streamingTweets = db.getCollection("streamingTweets");
		if(!streamingTweetsIndexesEnsured) {
			streamingTweets.createIndex(new BasicDBObject("id", 1));
			streamingTweets.createIndex(new BasicDBObject("user.id", 1));
			streamingTweetsIndexesEnsured = true;
		}
		return streamingTweets;
	}

	@Override
	public Iterator<Status> iterateTweetsWithUnprocessedUser() {
		final DBCursor tweetsWithUnprocressedUser = getCollection().find(
				new BasicDBObject(AIC_PROCESSED_USER, new BasicDBObject("$exists",
						false)));
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

			@Override
			public boolean hasNext() {
				return dbos.hasNext();
			}

			@Override
			public StatusRange next() {
				DBObject o = dbos.next();
				long min = ((Number) o.get("min_tweet")).longValue();
				long max = ((Number) o.get("max_tweet")).longValue();
				//niedrigere long-werte werden von JSON.parse in int übersetzt und sind dann
				//so in der DB. also muss man sicherheitshalber so auf den long-wert kommen. 
				long userId = ((Number) o.get("_id")).longValue();
				return new StatusRange(userId, min, max);
			}
		};
	}

	private DBCollection getUserTweetsCollection() {
		DBCollection userTweets = db.getCollection("user_tweets");
		if(!userTweetsIndexesEnsured) {
			userTweets.createIndex(new BasicDBObject("id", 1));
		}
		return userTweets;
	}
}
