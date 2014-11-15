package at.tuwien.aic2014.gr3.sql;

import at.tuwien.aic2014.gr3.shared.TweetProcessing;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.h2.tools.DeleteDbFiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    public void safeTweetIntoSQL(String rawJSON) throws IOException{
        InputStream is =
                getClass().getClassLoader().getResourceAsStream("at.tuwien.aic2014.gr3.sql/sample-json.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String jsonTxt = in.readLine();
        while(jsonTxt != null) {
            JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonTxt);
            JSONObject user = json.getJSONObject("user");
            long id = json.getLong("id");
            String name = user.getString("name");
            String screen_name = user.getString("screen_name");
            String location = user.getString("location");
            String description = json.getString("text");
            boolean protected_value = user.getBoolean("protected");
            boolean verified = user.getBoolean("verified");
            int followers_count = user.getInt("followers_count");
            int friends_count = user.getInt("friends_count");
            int retweet_count = json.getInt("retweet_count");

            System.out.println("Id: " + id);
            System.out.println("Retweet_count: " + retweet_count);
            System.out.println("Screen_name: " + screen_name);
            jsonTxt = in.readLine();
        }
    }


    //@Override
    public void initializeSQLDatabase() {
        try {
            Statement stat = conn.createStatement();
            stat.execute("create table test(id bigint primary key, name varchar(255),screen_name varchar(255)," +
                    "location varchar(255), description varchar(255),protected boolean," +
                    "verified boolean,followers_count integer,friends_count integer, listed_count integer," +
                    " favourites_count integer, created_at datetime,language varchar(255)," +
                    "last_time_synched datetime)");

            stat.execute("insert into test values(1,'Hello','','','',false,false,0,10,0,0,null,'ru',null)");
            stat.execute("insert into test values(2,'Test','','','',false,false,0,10,0,0,null,'ru',null)");
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
