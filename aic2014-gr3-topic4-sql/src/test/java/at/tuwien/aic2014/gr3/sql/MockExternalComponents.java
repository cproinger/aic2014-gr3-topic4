package at.tuwien.aic2014.gr3.sql;

import org.junit.rules.ExternalResource;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import at.tuwien.aic2014.gr3.shared.TweetRepository;

@Configuration
@Profile("active-mocks")
public class MockExternalComponents extends ExternalResource {


    TweetRepository tweetRepo = Mockito.mock(TweetRepository.class);
    
	
	@Bean
	@Primary
	public TweetRepository tweetRepo() {
		return tweetRepo;
	}
	
	@Override
	protected void after() {
		Mockito.reset(tweetRepo);
	}
}
