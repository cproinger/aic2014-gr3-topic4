package at.tuwien.aic2014.gr3.sql;

import at.tuwien.aic2014.gr3.domain.TwitterUser;
import at.tuwien.aic2014.gr3.shared.RepositoryException;
import at.tuwien.aic2014.gr3.shared.RepositoryIterator;
import at.tuwien.aic2014.gr3.shared.TwitterUserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Repository
public class SqlUserRepository implements TwitterUserRepository {

    private static Logger log = Logger.getLogger(SqlUserRepository.class);

    private static final long SQL_QUERY_LIMIT = 50;

    private static final String USER_TABLE_NAME = "TwitterUser";

    private static final String ID_COL_NAME = "id";
    private static final String NAME_COL_NAME = "name";
    private static final String SCREEN_NAME_COL_NAME = "screen_name";
    private static final String LOCATION_COL_NAME = "location";
    private static final String URL_COL_NAME = "url";
    private static final String DESCRIPTION_COL_NAME = "description";
    private static final String PROTECTED_COL_NAME = "protected";
    private static final String VERIFIED_COL_NAME = "verified";
    private static final String FOLLOWERS_COUNT_COL_NAME = "followers_count";
    private static final String FRIENDS_COUNT_COL_NAME = "friends_count";
    private static final String LISTED_COUNT_COL_NAME = "listed_count";
    private static final String FAVOURITES_COUNT_COL_NAME = "favourites_count";
    private static final String CREATED_AT_COL_NAME = "created_at";
    private static final String LANGUAGE_COL_NAME = "language";
    private static final String LAST_TIME_SYNCHED_COL_NAME = "last_time_synched";

    @Autowired
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public long count() throws RepositoryException {
    	try(Connection connection = dataSource.getConnection();
                ResultSet rs = connection.createStatement().executeQuery("select count(*) from " + USER_TABLE_NAME))  {
    		rs.next();
    		return rs.getLong(1);
    	} catch (SQLException e) {
			throw new RepositoryException("sql-error", e);
		}
    }

    @Override
    public TwitterUser save(TwitterUser user) throws RepositoryException {
        if (exists(user.getId())) {
            return user;
        }

        try {
            try(Connection connection = dataSource.getConnection();
                PreparedStatement stat = connection.prepareStatement(
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
            throw new RepositoryException("error saving tweetUser", e);
        }

        return user;
    }

    @Override
    public TwitterUser readById(long userId) throws RepositoryException {
        String query = String.format(
                "SELECT * " +
                        "FROM %s " +
                        "WHERE id = ?", USER_TABLE_NAME);

        try(Connection connection = dataSource.getConnection();
                PreparedStatement stat = connection.prepareStatement(query)) {
            stat.setLong(1, userId);

            try(ResultSet resultSet = stat.executeQuery()) {
                if (resultSet.next()) {
                    return twitterUserFromResultSet (resultSet);
                }
            }
        }
        catch (SQLException e) {
            throw new RepositoryException("SQL error", e);
        }

        return null;
    }

    @Override
    public RepositoryIterator<TwitterUser> readAll() throws RepositoryException {
        //Simulate pagination for large result sets.
        return new RepositoryIterator<TwitterUser>() {

            private long offset = 0;
            private Iterator<TwitterUser> wrappedIt;

            @Override
            public boolean hasNext() throws RepositoryException {
                if (wrappedIt == null || !wrappedIt.hasNext()) {
                    wrappedIt = readAllNextPage(offset);
                    return wrappedIt != null && wrappedIt.hasNext();
                }
                else {
                    return true;
                }
            }

            @Override
            public TwitterUser next() {
                offset++;
                return wrappedIt.next();
            }
        };
    }

    private Iterator<TwitterUser> readAllNextPage(long offset) throws RepositoryException {
        log.debug("Reading next page for TwitterUser at offset " + offset);

        String query = String.format(
                "SELECT *" +
                        "FROM %s " +
                        "LIMIT %d " +
                        "OFFSET %d", USER_TABLE_NAME, SQL_QUERY_LIMIT, offset);

        try(Connection connection = dataSource.getConnection();
                PreparedStatement stat = connection.prepareStatement(query);
                ResultSet resultSet = stat.executeQuery()) {
            List<TwitterUser> twitterUsers = new ArrayList<>();

            while (resultSet.next()) {
                twitterUsers.add(twitterUserFromResultSet(resultSet));
            }

            log.debug("Found " + twitterUsers.size() + " TwitterUsers at offset " + offset);

            return twitterUsers.iterator();
        } catch (SQLException e) {
            throw new RepositoryException("SQL error", e);
        }
    }


    private boolean exists(long userId) throws RepositoryException {
        String query = String.format(
                "SELECT count(*) " +
                        "FROM %s " +
                        "WHERE id = ?", USER_TABLE_NAME);

        try(Connection connection = dataSource.getConnection();
                PreparedStatement stat = connection.prepareStatement(query)) {
            stat.setLong(1, userId);

            try(ResultSet resultSet = stat.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        catch (SQLException e) {
            throw new RepositoryException("SQL error", e);
        }

        return false;
    }

    private TwitterUser twitterUserFromResultSet(ResultSet rs) throws SQLException {
        TwitterUser twitterUser = new TwitterUser();

        twitterUser.setId(rs.getLong(ID_COL_NAME));
        twitterUser.setName(rs.getString(NAME_COL_NAME));
        twitterUser.setScreenName(rs.getString(SCREEN_NAME_COL_NAME));
        twitterUser.setLocation(rs.getString(LOCATION_COL_NAME));
        twitterUser.setUrl(rs.getString(URL_COL_NAME));
        twitterUser.setDescription(rs.getString(DESCRIPTION_COL_NAME));
        twitterUser.setIsProtected(rs.getBoolean(PROTECTED_COL_NAME));
        twitterUser.setIsVerified(rs.getBoolean(VERIFIED_COL_NAME));
        twitterUser.setFollowersCount(rs.getInt(FOLLOWERS_COUNT_COL_NAME));
        twitterUser.setFriendsCount(rs.getInt(FRIENDS_COUNT_COL_NAME));
        twitterUser.setListedCount(rs.getInt(LISTED_COUNT_COL_NAME));
        twitterUser.setFavouritesCount(rs.getInt(FAVOURITES_COUNT_COL_NAME));
        twitterUser.setCreatedAt(toJavaDate(rs.getDate(CREATED_AT_COL_NAME)));
        twitterUser.setLanguage(rs.getString(LANGUAGE_COL_NAME));
        twitterUser.setLastTimeSynched(toJavaDate(rs.getDate(LAST_TIME_SYNCHED_COL_NAME)));

        return twitterUser;
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

    private Date toJavaDate (java.sql.Date date) {
        return date == null ? null : new Date(date.getTime());
    }

	@PostConstruct
    public void initSQL() throws RepositoryException {
        try(Connection connection = dataSource.getConnection()) {
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
            throw new RepositoryException("error creating user-table", e);
        }
    }

    public void getResults() throws SQLException {
        try(Connection connection = dataSource.getConnection();
                Statement stat = connection.createStatement();
                ResultSet rs = stat.executeQuery("Select * from usersTwitter")) {
            while(rs.next()){
                System.out.println(rs.getString("name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("error getting results", e);
        }

    }
}
