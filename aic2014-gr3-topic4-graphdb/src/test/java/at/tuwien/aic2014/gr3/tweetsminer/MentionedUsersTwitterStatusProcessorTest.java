package at.tuwien.aic2014.gr3.tweetsminer;

import at.tuwien.aic2014.gr3.graphdb.TwitterUserRelationships;
import at.tuwien.aic2014.gr3.shared.TwitterStatusProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import twitter4j.UserMentionEntity;

public class MentionedUsersTwitterStatusProcessorTest extends TweetProcessorTest {

    @Autowired
    private TwitterStatusProcessor mentionedUsersStatusProcessor;

    @Test
    public void testProcess() throws Exception {
        mentionedUsersStatusProcessor.process(getTestingTwitterStatus());

        for (UserMentionEntity u : getTestingTwitterStatus().getUserMentionEntities()) {
            assertUserRelationship(getTestingTwitterStatus().getUser().getId(),
                    TwitterUserRelationships.MENTIONED.name(), u.getId(), 1);
        }
    }
}