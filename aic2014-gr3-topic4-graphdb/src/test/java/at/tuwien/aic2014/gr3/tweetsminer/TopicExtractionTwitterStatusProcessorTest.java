package at.tuwien.aic2014.gr3.tweetsminer;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import at.tuwien.aic2014.gr3.shared.TwitterStatusProcessor;

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