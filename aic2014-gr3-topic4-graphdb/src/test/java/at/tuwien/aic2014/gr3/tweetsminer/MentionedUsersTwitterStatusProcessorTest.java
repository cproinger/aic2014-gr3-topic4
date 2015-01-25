package at.tuwien.aic2014.gr3.tweetsminer;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import twitter4j.UserMentionEntity;
import at.tuwien.aic2014.gr3.graphdb.TwitterUserRelationships;
import at.tuwien.aic2014.gr3.shared.TwitterStatusProcessor;

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