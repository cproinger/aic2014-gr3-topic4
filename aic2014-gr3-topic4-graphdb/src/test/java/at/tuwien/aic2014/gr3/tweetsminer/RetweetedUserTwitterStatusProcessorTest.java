package at.tuwien.aic2014.gr3.tweetsminer;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import at.tuwien.aic2014.gr3.graphdb.TwitterUserRelationships;
import at.tuwien.aic2014.gr3.shared.TwitterStatusProcessor;

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