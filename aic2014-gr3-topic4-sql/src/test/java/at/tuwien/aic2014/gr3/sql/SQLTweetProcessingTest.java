package at.tuwien.aic2014.gr3.sql;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

    @Test
    public void testTextProcessing(){
        try {
            InputStream is =
                    getClass().getClassLoader().getResourceAsStream("at.tuwien.aic2014.gr3.sql/sample-json.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String jsonTxt = in.readLine();
            while(jsonTxt != null) {
                sqlTweet.safeTweetIntoSQL(jsonTxt);
                jsonTxt = in.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
