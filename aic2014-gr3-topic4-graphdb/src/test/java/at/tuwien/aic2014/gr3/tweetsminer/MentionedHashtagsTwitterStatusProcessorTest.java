package at.tuwien.aic2014.gr3.tweetsminer;

import at.tuwien.aic2014.gr3.shared.TwitterStatusProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import twitter4j.HashtagEntity;

public class MentionedHashtagsTwitterStatusProcessorTest extends TweetProcessorTest {

    @Autowired
    private TwitterStatusProcessor mentionedHashtagsStatusProcessor;

    @Test
    public void testProcess() throws Exception {
        mentionedHashtagsStatusProcessor.process(getTestingTwitterStatus());

        for (HashtagEntity hashtagEntity : getTestingTwitterStatus().getHashtagEntities()) {
            assertUserRelationshipHashtag(getTestingTwitterStatus().getUser().getId(), hashtagEntity.getText(), 1);
        }
    }
}