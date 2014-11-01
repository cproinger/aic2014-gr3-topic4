package at.tuwien.aic2014.gr3.docstore;

import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;

@Configuration
@ComponentScan(basePackages = "at.tuwien.aic2014.gr3.docstore")
@PropertySource("classpath:/META-INF/topic4/docstore.properties")
public class DocStoreConfig {

	@Value("${topic4.docstore.dbname}")
	private String dbName = "aicdocstore";
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
	    return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public MongoClient mongoClient() throws UnknownHostException {
		MongoClient mongo = new MongoClient();
		mongo.setWriteConcern(WriteConcern.SAFE);
		return mongo;
	}
	
	@Bean
	public DB mongoDB() throws UnknownHostException {
		return mongoClient().getDB(dbName);
	}
	
	@Bean
	public MongoDbFactory mongoDbFactory() throws UnknownHostException {
		return new SimpleMongoDbFactory(mongoClient(), dbName);
	}
	
	@Bean
	public MongoTemplate mongoTemplate() throws UnknownHostException {
		return new MongoTemplate(mongoDbFactory());
	}
}
