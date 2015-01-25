package at.tuwien.aic2014.gr3.tweetsminer;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import twitter4j.HashtagEntity;
import at.tuwien.aic2014.gr3.shared.TwitterStatusProcessor;

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