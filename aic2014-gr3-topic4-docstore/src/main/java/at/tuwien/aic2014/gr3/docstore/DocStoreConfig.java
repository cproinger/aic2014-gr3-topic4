package at.tuwien.aic2014.gr3.docstore;

import java.net.UnknownHostException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.MongoClient;

@Configuration
@ComponentScan(basePackages = "at.tuwien.aic2014.gr3.docstore")
public class DocStoreConfig {

	@Bean
	public MongoClient mongoClient() throws UnknownHostException {
		MongoClient mongo = new MongoClient();
		return mongo;
	}
	
	@Bean
	public MongoDbFactory mongoDbFactory() throws UnknownHostException {
		return new SimpleMongoDbFactory(mongoClient(), "aicdocstore");
	}
	
	@Bean
	public MongoTemplate mongoTemplate() throws UnknownHostException {
		return new MongoTemplate(mongoDbFactory());
	}
}
