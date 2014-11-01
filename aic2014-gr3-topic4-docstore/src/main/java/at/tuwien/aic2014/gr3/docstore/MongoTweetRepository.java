package at.tuwien.aic2014.gr3.docstore;

import java.util.Date;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import at.tuwien.aic2014.gr3.shared.TweetRepository;

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
		getCollection().save(obj);
	}

	private DBCollection getCollection() {
		return db.getCollection("streamingTweets");
	}

	@Override
	public Iterator<Status> iterateTweetsWithUnprocessedUser() {
		final DBCursor tweetsWithUnprocressedUser = getCollection().find(
				new BasicDBObject(AIC_PROCESSED_USER, new BasicDBObject("$exists",
						false)));
		LOG.info("tweetsWithUnprocressedUser: " + tweetsWithUnprocressedUser.count());
		
		return new MarkProcessedStatusIterator(tweetsWithUnprocressedUser, AIC_PROCESSED_USER);
	}
}
