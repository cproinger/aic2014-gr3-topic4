package at.tuwien.aic2014.gr3.docstore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import at.tuwien.aic2014.gr3.shared.TweetRepository;

import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

@Repository
public class MongoTweetRepository implements TweetRepository {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private MongoClient mongoClient;
	
	/**
	 * @param json
	 * 		
	 */
	@Override
	public void save(String json) {
		//http://stackoverflow.com/a/22177940/775513: says that this will not handle dates correctly
		//but it's a start
		DBObject obj = (DBObject) JSON.parse(json);
		mongoClient.getDB("aicdocstore").getCollection("stuff").save(obj);
		//mongoTemplate.save(obj, "streamingTweets");
	}
}
