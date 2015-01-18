package at.tuwien.aic2014.gr3.tweetsminer.filters;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertArrayEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:tweetsMinerTestContext.xml")
public class TweetFilterChainTest {

    @Autowired
    private TweetFilterChain<String[],String> tweetFilterChain;

    @Test
    public void testPipe() throws Exception {
        assertArrayEquals(new String[] {"testing"},
                tweetFilterChain.filter(new DataCarrier<>(
                        "RT @user: testing !@user me@mail --#user @ #user http://g:8080")).getData());
    }
}