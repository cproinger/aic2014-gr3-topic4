package at.tuwien.aic2014.gr3.tweetsminer.filters;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:tweetsMinerTestContext.xml")
public class RemoveUrlsTweetFilterTest {


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