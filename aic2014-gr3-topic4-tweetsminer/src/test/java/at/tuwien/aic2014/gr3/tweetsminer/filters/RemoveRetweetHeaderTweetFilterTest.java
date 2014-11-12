package at.tuwien.aic2014.gr3.tweetsminer.filters;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:tweetsMinerTestContext.xml")
public class RemoveRetweetHeaderTweetFilterTest {

    private static final String TEST_TEXT = "test stuff";

    @Autowired
    private RemoveRetweetHeaderTweetFilter removeRetweetUserFilter;

    @Test
    public void testPipe() throws Exception {
        assertEquals(TEST_TEXT,
                removeRetweetUserFilter.filter(new DataCarrier<>(
                        "RT @user: " + TEST_TEXT)).getData());
    }
}