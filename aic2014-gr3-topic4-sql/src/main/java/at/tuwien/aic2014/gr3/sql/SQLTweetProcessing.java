package at.tuwien.aic2014.gr3.sql;

import at.tuwien.aic2014.gr3.shared.TweetProcessing;
import org.h2.tools.DeleteDbFiles;
import twitter4j.JSONException;
import twitter4j.JSONObject;

import java.sql.*;

/**
 * Created with IntelliJ IDEA.
 * User: putzchri
 * Date: 02.11.14
 * Time: 12:59
 * To change this template use File | Settings | File Templates.
 */
public class SQLTweetProcessing implements TweetProcessing {
    Connection conn;
    String url = "jdbc:h2:~/name";
    String username = "sa";
    String password = "";

    @Override
    public void safeTweetIntoSQL(String rawJSON) {
        try {
            JSONObject obj = new JSONObject("dump.json");
            String id = obj.getJSONObject("id").toString();
            System.out.println(id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //@Override
    public void initializeSQLDatabase() {
        try {
            Statement stat = conn.createStatement();
            stat.execute("create table test(id int primary key, name varchar(255),screen_name varchar(255)," +
                    "location varchar(255), url varchar(255),description varchar(255),protected boolean," +
                    "verified boolean,followers_count integer,friends_count integer, listed_count integer," +
                    " favourites_count integer, created_at datetime,language varchar(255)," +
                    "last_time_synched datetime)");

            stat.execute("insert into test values(1,'Hello','','','http://t.co/wBsL7knD3R','',false,false,0,10,0,0,null,'ru',null)");
            stat.execute("insert into test values(2,'Test','','','http://t.co/wBsL7knD3R','',false,false,0,10,0,0,null,'ru',null)");
            ResultSet rs = stat.executeQuery("select * from test");
            while (rs.next()) {
                System.out.println(rs.getString("name"));
            }
            stat.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //@Override
    public void connectToSQL() throws Exception {
        DeleteDbFiles.execute("~", "name", true);
        conn = DriverManager.getConnection(url, username, password);
    }

    //@Override
    public void closeDownConnection() throws SQLException {
        conn.close();
    }
}
