package at.tuwien.aic2014.gr3.docstore;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mongodb.BasicDBObject;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DocStoreConfig.class)
public class MongoTweetRepositoryTest {

	@Autowired
	private MongoTweetRepository repo;
	
	@Test
	public void test() {
		repo.save("{ test : \"test\" }");
	}
}
