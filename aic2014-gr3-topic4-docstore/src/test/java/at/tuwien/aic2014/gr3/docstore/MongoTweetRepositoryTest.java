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
		repo.save("{ test : \"test\" }");
	}

	@Test
	public void testIterateTweetsWithUnprocessedUser() {
		repo.save(" { id : 123, text : \"test\"}");
		repo.save(" { id : 1234, text : \"test\", aic_processed_user : true }");

		assertEquals(1, iterateAndMarkProcessed());
		assertEquals(0, iterateAndMarkProcessed());
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
