package at.tuwien.aic2014.gr3.twitter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import at.tuwien.aic2014.gr3.docstore.DocStoreConfig;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStreamFactory;

@Configuration
@ComponentScan(basePackageClasses = TwitterConfig.class)
@Import(DocStoreConfig.class)
public class TwitterConfig {

	@Bean
	public TwitterStreamFactory twitterStreamFactory() {
		return new TwitterStreamFactory();
	}
	
	@Bean
	public Twitter twitterFactory() {
		return TwitterFactory.getSingleton();
	}
}
