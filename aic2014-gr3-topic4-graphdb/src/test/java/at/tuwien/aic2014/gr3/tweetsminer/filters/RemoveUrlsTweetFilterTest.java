package at.tuwien.aic2014.gr3.tweetsminer.filters;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import at.tuwien.aic2014.gr3.graphdb.GraphDBTestBase;

public class RemoveUrlsTweetFilterTest extends GraphDBTestBase {


    @Autowired
    private RemoveUrlsTweetFilter removeUrlsTweetFilterFilter;

    @Test
    public void testPipe() throws Exception {
        assertEquals(" testing url tweet ",
                removeUrlsTweetFilterFilter.filter(new DataCarrier<>(
                        "http://example.com testing http:// url http://mal---235.com " +
                                "tweet http://d@g:8080")).getData());
    }
}