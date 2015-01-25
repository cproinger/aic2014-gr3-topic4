package at.tuwien.aic2014.gr3.tweetsminer.filters;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import at.tuwien.aic2014.gr3.graphdb.GraphDBTestBase;


public class RemoveHashtagsTweetFilterTest extends GraphDBTestBase {


    @Autowired
    private RemoveHashtagsTweetFilter removeHashtagMentionsFilter;

    @Test
    public void testPipe() throws Exception {
        assertEquals("testing ! me#mail -- # ",
                removeHashtagMentionsFilter.filter(new DataCarrier<>(
                        "#user testing !#user me#mail --#user # #user")).getData());
    }
}