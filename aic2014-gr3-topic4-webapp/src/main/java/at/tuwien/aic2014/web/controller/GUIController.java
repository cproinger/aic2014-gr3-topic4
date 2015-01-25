package at.tuwien.aic2014.web.controller;

import java.util.Collection;
import java.util.List;

import at.tuwien.aic2014.gr3.domain.Advertisment;
import at.tuwien.aic2014.gr3.domain.PotentialInterest;
import at.tuwien.aic2014.gr3.domain.TwitterUser;
import at.tuwien.aic2014.gr3.domain.UserTopic;

	
public interface GUIController {
	public List<TwitterUser> getInterstedUsers(boolean ascending, int processedCountMoreThan, int maxResults);
	public TwitterUser getUserById(long id);
	public TwitterUser getUserByName(String name);
	public List<UserTopic> getExistingInterestsForUser(long userId);
	public List<PotentialInterest> getPotetialInterestsForUser(long userId) ;
	public Collection<Advertisment> getAdsForUserExistingInterests(long userId);
	public Collection<Advertisment> getAdsForUserPotentiaPotentialInterests(long userId);
	

}
