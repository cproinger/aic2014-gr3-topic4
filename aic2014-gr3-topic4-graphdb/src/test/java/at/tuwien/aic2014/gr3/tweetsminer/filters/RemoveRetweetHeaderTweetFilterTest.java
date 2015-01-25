package at.tuwien.aic2014.gr3.tweetsminer.filters;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import at.tuwien.aic2014.gr3.graphdb.GraphDBTestBase;

public class RemoveRetweetHeaderTweetFilterTest extends GraphDBTestBase {

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