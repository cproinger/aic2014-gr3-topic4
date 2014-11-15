
package at.tuwien.aic2014.gr3.docstore;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import twitter4j.Status;
import at.tuwien.aic2014.gr3.domain.StatusRange;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DocStoreConfig.class)
public class MongoTweetRepositoryTest {

	@Autowired
	private DB db;

	@After
	public void tearDownCollection() {
		if ("aicdocstore-test".equals(db.getName())) {
			db.getCollection("streamingTweets").drop();
		}
	}

	@Autowired
	private MongoTweetRepository repo;

	@Test
	public void test() {
		repo.save("{ test : \"test\", user : { name : \"test\"} }");
	}

	@Test
	public void testIterateTweetsWithUnprocessedUser() {
		repo.save(simpleTweet(124L).toString());
		repo.save(simpleTweet(125L).append("aic_processed_user", true).toString());

		assertEquals(1, iterateAndMarkProcessed());
		assertEquals(0, iterateAndMarkProcessed());
	}
	
	@Test
	public void testUpsertTweet() {
		long inital = repo.countTweets();
		repo.save(simpleTweet(1L).toString());
		repo.save(simpleTweet(1L).toString());
		
		assertEquals("expected one more tweet", inital + 1, repo.countTweets());
	}

	private BasicDBObject simpleTweet(long tweetId) {
		BasicDBObject o = new BasicDBObject("text", "test")
				.append("user",	new BasicDBObject("name", "testUser").append("id", 1987L));
		o.append("id", tweetId);
		return o;
	}
	
	@Test
	public void testCount() {
		long initialCount = repo.countTweets();
		repo.save(simpleTweet(987654L).toString());
		assertEquals("expected one more tweet", initialCount + 1, repo.countTweets());
	}

	private int iterateAndMarkProcessed() {
		int i = 0;
		for (Iterator<Status> it = repo.iterateTweetsWithUnprocessedUser(); it
				.hasNext();) {
			it.next();
			it.remove();
			i++;
		}
		return i;
	}
}
