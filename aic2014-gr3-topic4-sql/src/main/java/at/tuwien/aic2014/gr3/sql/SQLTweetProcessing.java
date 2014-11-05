package at.tuwien.aic2014.gr3.sql;

import at.tuwien.aic2014.gr3.shared.TweetProcessing;
import twitter4j.JSONException;
import twitter4j.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: putzchri
 * Date: 02.11.14
 * Time: 12:59
 * To change this template use File | Settings | File Templates.
 */
public class SQLTweetProcessing implements TweetProcessing {


    @Override
    public void safeTweetIntoSQL(String rawJSON) {
        try {
            JSONObject obj = new JSONObject("test.txt");
            String id = obj.getJSONObject("id").toString();
            System.out.println(id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
