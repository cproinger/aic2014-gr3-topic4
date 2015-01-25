package at.tuwien.aic2014.gr3.tweetsminer;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import at.tuwien.aic2014.gr3.graphdb.TwitterUserRelationships;
import at.tuwien.aic2014.gr3.shared.TwitterStatusProcessor;

public class RepliedToTwitterStatusProcessorTest extends TweetProcessorTest {

    @Autowired
    private TwitterStatusProcessor repliedToStatusProcessor;

    @Test
    public void testProcess() throws Exception {
        repliedToStatusProcessor.process(getTestingTwitterStatus());

        assertUserRelationship(getTestingTwitterStatus().getUser().getId(),
                TwitterUserRelationships.REPLIED_TO.name(),
                getTestingTwitterStatus().getInReplyToUserId(),
                1);
    }
}