package at.tuwien.aic2014.gr3.tweetsminer;

import at.tuwien.aic2014.gr3.shared.TwitterStatusProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class TopicExtractionTwitterStatusProcessorTest extends TweetProcessorTest {

    @Autowired
    private TwitterStatusProcessor topicExtractionStatusProcessor;

    @Test
    public void testProcess() throws Exception {
        topicExtractionStatusProcessor.process(getTestingTwitterStatus());

        long userId = getTestingTwitterStatus().getUser().getId();

        assertUserRelationshipTopic(userId, "testtopic", 2);
        assertUserRelationshipTopic(userId, "Mike Tyson", 1);
    }
}