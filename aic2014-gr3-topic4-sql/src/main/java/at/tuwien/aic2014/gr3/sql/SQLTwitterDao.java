package at.tuwien.aic2014.gr3.sql;

import java.util.List;

import at.tuwien.aic2014.gr3.domain.TwitterUser;

public interface SQLTwitterDao {
	
	public List<TwitterUser> findAll();

}
