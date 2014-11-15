package at.tuwien.aic2014.gr3.sql;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import at.tuwien.aic2014.gr3.domain.TwitterUser;

@Repository
public class SqlUserRepository {

	//jdbc template vielleicht gscheiter? oder gleich JPA?
	@Autowired
	private DataSource dataSource;
	
	public long count() {
		return -1L;
	}
	
	public void save(TwitterUser user) {
		try(Connection con = dataSource.getConnection()) {
			
			//TODO. 
			
		} catch (SQLException e) {
			throw new RuntimeException("sql-error", e);
		}
	}
}
