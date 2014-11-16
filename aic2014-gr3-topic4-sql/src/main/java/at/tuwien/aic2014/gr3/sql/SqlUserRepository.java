package at.tuwien.aic2014.gr3.sql;

import at.tuwien.aic2014.gr3.domain.TwitterUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Repository
public class SqlUserRepository {

    //jdbc template vielleicht gscheiter? oder gleich JPA?
    @Autowired
    private DataSource dataSource;

    private Connection con;

    public long count() {
        return -1L;
    }

    public void save(TwitterUser user) {
        try {
            this.con = dataSource.getConnection();
            Statement stat = con.createStatement();
            stat.execute("insert into usersTwitter values(" + user.getId() + ",'" + user.getName() + "','" + user.getScreenName() + "','"
                    + user.getLocation() + "'" + ",'" + user.getUrl() + "','" + user.getDescription() + "'," + user.getIsProtected() +
                    "," + user.getIsVerified() + "," + user.getFollowersCount() + "," + user.getFriendsCount() + ","
                    + user.getListedCount() + "," + user.getFavouritesCount() + ","
                    + user.getCreatedAt() + ",'" + user.getLanguage() + "'," + user.getLastTimeSynched() + ")");
            stat.close();
        } catch (SQLException e) {
            throw new RuntimeException("sql-error", e);
        }
    }

    public void initSQL() {
        try {
            this.con = dataSource.getConnection();
            Statement stat = con.createStatement();
            stat.execute("create table usersTwitter(id bigint primary key, name varchar(255),screen_name varchar(255)," +
                    "location varchar(255),url varchar(255), description varchar(255),protected boolean," +
                    "verified boolean,followers_count integer,friends_count integer, listed_count integer," +
                    " favourites_count integer, created_at datetime,language varchar(255)," +
                    "last_time_synched datetime)");
            stat.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeH2SQL(){
        try {
            this.con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getResults() {
        try {
            Statement stat = con.createStatement();
            ResultSet rs = stat.executeQuery("Select * from usersTwitter");
            while(rs.next()){
                System.out.println(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
