package at.tuwien.aic2014.gr3.docstore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

@Repository
public class TweetRepository {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private MongoClient mongoClient;
	
	/**
	 * @param json
	 * 		
	 */
	public void save(String json) {
		DBObject obj = (DBObject) JSON.parse(json);
		mongoClient.getDB("aicdocstore").getCollection("stuff").save(obj);
		//mongoTemplate.save(obj, "streamingTweets");
	}
}
