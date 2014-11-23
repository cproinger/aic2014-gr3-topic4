package at.tuwien.aic2014.gr3.tweetsminer.filters;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:tweetsMinerTestContext.xml")
public class RemoveHashtagsTweetFilterTest {


    @Autowired
    private RemoveHashtagsTweetFilter removeHashtagMentionsFilter;

    @Test
    public void testPipe() throws Exception {
        assertEquals("testing ! me#mail -- # ",
                removeHashtagMentionsFilter.filter(new DataCarrier<>(
                        "#user testing !#user me#mail --#user # #user")).getData());
    }
}