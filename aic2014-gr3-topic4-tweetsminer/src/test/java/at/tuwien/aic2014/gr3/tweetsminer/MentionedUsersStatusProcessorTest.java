package at.tuwien.aic2014.gr3.tweetsminer;

import at.tuwien.aic2014.gr3.dao.TwitterUserRelationships;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:tweetsMinerTestContext.xml")
public class MentionedUsersStatusProcessorTest extends TweetProcessorTest {

    @Autowired
    private TwitterStatusProcessor mentionedUsersStatusProcessor;

    @Test
    public void testProcess() throws Exception {
        mentionedUsersStatusProcessor.process(getTestingTwitterStatus());

        assertUserRelationship(100, TwitterUserRelationships.MENTIONED.name(), 101, 1);
        assertUserRelationship(100, TwitterUserRelationships.MENTIONED.name(), 102, 1);
    }
}