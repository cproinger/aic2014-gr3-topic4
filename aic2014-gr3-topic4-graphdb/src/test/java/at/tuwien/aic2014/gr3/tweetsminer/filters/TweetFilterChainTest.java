package at.tuwien.aic2014.gr3.tweetsminer.filters;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import at.tuwien.aic2014.gr3.graphdb.GraphDBTestBase;

public class TweetFilterChainTest extends GraphDBTestBase {

    @Autowired
    private TweetFilterChain<String[],String> tweetFilterChain;

    @Test
    public void testPipe() throws Exception {
        assertArrayEquals(new String[] {"testing"},
                tweetFilterChain.filter(new DataCarrier<>(
                        "RT @user: testing !@user me@mail --#user @ #user http://g:8080")).getData());
    }
}