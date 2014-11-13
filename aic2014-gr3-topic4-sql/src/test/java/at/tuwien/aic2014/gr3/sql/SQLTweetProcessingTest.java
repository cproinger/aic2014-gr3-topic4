package at.tuwien.aic2014.gr3.sql;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by buzz on 02.11.2014.
 */

public class SQLTweetProcessingTest {

    @Autowired
    private at.tuwien.aic2014.gr3.sql.SQLTweetProcessing sqlTweet = new at.tuwien.aic2014.gr3.sql.SQLTweetProcessing();


    @Test
    public void testSQLProcessing(){
        try {
            sqlTweet.connectToSQL();
            sqlTweet.initializeSQLDatabase();
            //sqlTweet.safeTweetIntoSQL("test");
            sqlTweet.closeDownConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
