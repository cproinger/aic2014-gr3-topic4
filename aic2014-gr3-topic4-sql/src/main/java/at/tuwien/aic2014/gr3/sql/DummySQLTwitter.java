package at.tuwien.aic2014.gr3.sql;

import java.util.ArrayList;
import java.util.List;

import at.tuwien.aic2014.gr3.domain.TwitterUser;

public class DummySQLTwitter implements SQLTwitterDao {

	@Override
	public List<TwitterUser> findAll() {
		ArrayList<TwitterUser> liste = new ArrayList<TwitterUser>();
		TwitterUser user = new TwitterUser();
//		user.setId(527746673439961088L);
//		user.setName("JL69");
//		liste.add(user);
		user = new TwitterUser();
		user.setId(314824070L);
		user.setName("KeenonAndKel");
		liste.add(user);
//		user = new TwitterUser();
//		user.setId(527746673414778880L);
//		user.setName("DanielShishkin");
//		liste.add(user);
		return liste;
	}

}
