package at.tuwien.aic2014.gr3.sql;

import at.tuwien.aic2014.gr3.shared.TweetRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by buzz on 02.11.2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SqlConfig.class, MockExternalComponents.class})
public class SQLTweetProcessingTest {

	@Autowired
    private SQLTweetProcessing processor;
    
    @Autowired
    private SqlUserRepository userRepo;
    
    /**
     * damit reset nach jedem test aufgerufen wird auf den mocks. 
     */
    @Rule
    public MockExternalComponents mocks;
    
    @Autowired
    private TweetRepository tweetRepo;


    @Test
    public void testSQLProcessing(){
    	List<Status> statuses = new ArrayList<Status>(); 
    	Status status = Mockito.mock(Status.class);
    	//TODO return test-data Mockito.when(status.getXXX()).thenReturn(...);
		statuses.add(status);
    	Mockito.when(tweetRepo.iterateTweetsWithUnprocessedUser()).thenReturn(statuses.iterator());
    	processor.processAll();
    	assertEquals("should be empty", 0, statuses.size());
    	//TODO verify created user. 
//        try {
//            sqlTweet.connectToSQL();
//            sqlTweet.initializeSQLDatabase();
//            //sqlTweet.safeTweetIntoSQL("test");
//            sqlTweet.closeDownConnection();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }
    
    @Test
    public void testTweetProcessing() throws IOException, TwitterException {
    	InputStream is =
                getClass().getResourceAsStream("/sample-json.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String line = null;
        ArrayList<Status> statuses = new ArrayList<Status>();
        while((line = in.readLine()) != null) {
        	Status status = (Status) TwitterObjectFactory.createStatus(line);
        	statuses.add(status);
        }
        Mockito.when(tweetRepo.iterateTweetsWithUnprocessedUser()).thenReturn(statuses.iterator());
        processor.processAll();
        assertEquals("should be empty", 0, statuses.size());
        //6 tweets haben offenbar keinen user. 
        assertEquals(1041, userRepo.count());
    }
}
