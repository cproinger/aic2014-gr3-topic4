package at.tuwien.aic2014.gr3.tweetsminer;

import at.tuwien.aic2014.gr3.graphdb.TwitterUserRelationships;
import at.tuwien.aic2014.gr3.shared.TwitterStatusProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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