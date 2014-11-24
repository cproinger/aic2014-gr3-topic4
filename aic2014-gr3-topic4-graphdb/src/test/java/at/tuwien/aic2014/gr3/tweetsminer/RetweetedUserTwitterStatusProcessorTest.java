package at.tuwien.aic2014.gr3.tweetsminer;

import at.tuwien.aic2014.gr3.dao.TwitterUserRelationships;
import at.tuwien.aic2014.gr3.shared.TwitterStatusProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:tweetsMinerTestContext.xml")
public class RetweetedUserTwitterStatusProcessorTest extends TweetProcessorTest {

    @Autowired
    private TwitterStatusProcessor retweetedUserStatusProcessor;

    @Test
    public void testProcess() throws Exception {
        retweetedUserStatusProcessor.process(getTestingTwitterStatus());

        assertUserRelationship(getTestingTwitterStatus().getUser().getId(),
                TwitterUserRelationships.RETWEETED.name(),
                getTestingTwitterStatus().getRetweetedStatus().getUser().getId(),
                1);
    }
}