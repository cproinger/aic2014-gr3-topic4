package at.tuwien.aic2014.gr3.tweetsminer.filters;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import at.tuwien.aic2014.gr3.graphdb.GraphDBTestBase;

public class SmartTokenizerTweetFilterTest extends GraphDBTestBase {


    @Autowired
    private SmartTokenizerTweetFilter smartTokenizerTweetFilter;

    @Value("classpath:en-ner-person.bin")
    private Resource res;

    @Value("classpath:en-token.bin")
    private Resource res2;

    @Test
    public void testPipe() throws Exception {
        assertArrayEquals(
                new String[]{"testing", "Donald", "Brown Arnold", "Harry O'Connel"},
                smartTokenizerTweetFilter.filter(new DataCarrier<>(
                        "This is,! testing? Donald and Brown Arnold. Harry O'Connel")).getData());
    }
}