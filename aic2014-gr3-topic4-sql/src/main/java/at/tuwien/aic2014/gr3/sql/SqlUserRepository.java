package at.tuwien.aic2014.gr3.sql;

import at.tuwien.aic2014.gr3.domain.TwitterUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.Date;

@Repository
public class SqlUserRepository {

    private static final String USER_TABLE_NAME = "TwitterUser";

	//jdbc template vielleicht gscheiter? oder gleich JPA?
    @Autowired
    private Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public long count() {
    	try(ResultSet rs = connection.createStatement().executeQuery("select count(*) from " + USER_TABLE_NAME))  {
    		rs.next();
    		return rs.getLong(1);
    	} catch (SQLException e) {
			throw new RuntimeException("sql-error", e);
		}
    }

    private boolean exists(long userId) {
        String query = String.format(
                "SELECT count(*) " +
                        "FROM %s " +
                        "WHERE id = ?", USER_TABLE_NAME);

        try(PreparedStatement stat = connection.prepareStatement(query)) {
            stat.setLong(1, userId);

            try(ResultSet resultSet = stat.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("SQL error", e);
        }

        return false;
    }


    public void save(TwitterUser user) {
        if (exists(user.getId())) {
            return; // TODO: should update
        }

        try {
            try(PreparedStatement stat = connection.prepareStatement(
                    "INSERT into " + USER_TABLE_NAME + " values("
                            + questionMarks(15) + ")");) {
                //            stat.execute("insert into usersTwitter values(" + user.getId() + ",'" + user.getName() + "','" + user.getScreenName() + "','"
                //                    + user.getLocation() + "'" + ",'" + user.getUrl() + "','" + user.getDescription() + "'," + user.getIsProtected() +
                //                    "," + user.getIsVerified() + "," + user.getFollowersCount() + "," + user.getFriendsCount() + ","
                //                    + user.getListedCount() + "," + user.getFavouritesCount() + ","
                //                    + user.getCreatedAt() + ",'" + user.getLanguage() + "'," + user.getLastTimeSynched() + ")");
                stat.setLong(1, user.getId());
                stat.setString(2, user.getName());
                stat.setString(3, user.getScreenName());
                stat.setString(4, user.getLocation());
                stat.setString(5, user.getUrl());
                stat.setString(6, user.getDescription());
                stat.setBoolean(7, user.getIsProtected());
                stat.setBoolean(8, user.getIsVerified());
                stat.setInt(9, user.getFollowersCount());
                stat.setInt(10, user.getFriendsCount());
                stat.setInt(11, user.getListedCount());
                stat.setInt(12, user.getFavouritesCount());
                stat.setDate(13,  toSqlDate(user.getCreatedAt()));
                stat.setString(14, user.getLanguage());
                stat.setDate(15,  toSqlDate(user.getLastTimeSynched()));
                stat.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("error saving tweetUser", e);
        }
    }

	private String questionMarks(int count) {
		assert count >= 1;
		StringBuilder sb = new StringBuilder("?");
		for(int i = 1; i < count; i++) {
			sb.append(", ?");
		}
		return sb.toString();
	}

	private java.sql.Date toSqlDate(Date createdAt) {
		if(createdAt == null) return null;
		return new java.sql.Date(createdAt.getTime());
	}

	@PostConstruct
    public void initSQL() {
        try {
            ResultSet rs = connection.createStatement().executeQuery(
                    "select * from information_schema.tables where upper(table_name) = upper('"
                            + USER_TABLE_NAME + "')");
            if(!rs.next()) {
                Statement stat = connection.createStatement();
                stat.execute("create table " + USER_TABLE_NAME + "("
                        + "id bigint primary key"
                        + ",name varchar(255)"
                        + ",screen_name varchar(255)"
                        + ",location varchar(255)"
                        + ",url varchar(255)"
                        + ",description varchar(255)"
                        + ",protected boolean"
                        + ",verified boolean"
                        + ",followers_count integer"
                        + ",friends_count integer"
                        + ",listed_count integer"
                        + ",favourites_count integer"
                        + ",created_at timestamp "
                        + ",language varchar(255)"
                        + ",last_time_synched timestamp )");
            }
        } catch (SQLException e) {
            throw new RuntimeException("error creating user-table", e);
        }
    }

    public void getResults() throws SQLException {
        Statement stat = connection.createStatement();
        try(ResultSet rs = stat.executeQuery("Select * from usersTwitter")) {
            while(rs.next()){
                System.out.println(rs.getString("name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("error getting results", e);
        }

    }
}
