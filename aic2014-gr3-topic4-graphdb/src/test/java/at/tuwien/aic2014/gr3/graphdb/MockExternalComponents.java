package at.tuwien.aic2014.gr3.graphdb;

import org.junit.rules.ExternalResource;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import at.tuwien.aic2014.gr3.domain.TwitterUser;
import at.tuwien.aic2014.gr3.shared.TweetRepository;
import at.tuwien.aic2014.gr3.shared.TwitterUserRepository;

@Configuration
@Profile("activate-mocks")
public class MockExternalComponents extends ExternalResource {


    private TweetRepository tweetRepo = Mockito.mock(TweetRepository.class);
    private TwitterUserRepository sqlUserRep = Mockito.mock(TwitterUserRepository.class, Mockito.RETURNS_SMART_NULLS);
	
    @Override
    protected void before() throws Throwable {
    	Mockito.when(sqlUserRep.readById(Mockito.anyLong())).thenAnswer(new Answer<TwitterUser>() {

			@Override
			public TwitterUser answer(InvocationOnMock invocation)
					throws Throwable {
				long id = (long) invocation.getArguments()[0];
				TwitterUser u = new TwitterUser();
				u.setId(id);
				return u;
			}
		});
    }
    
	@Bean
	@Primary
	public TweetRepository tweetRepo() {
		return tweetRepo;
	}
	
	@Bean
	@Primary
	public TwitterUserRepository twitterUserRepo() {
		return sqlUserRep;
	}
	
	@Override
	protected void after() {
		Mockito.reset(tweetRepo);
	}
}
