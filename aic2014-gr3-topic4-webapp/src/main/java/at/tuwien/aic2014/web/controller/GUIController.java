package at.tuwien.aic2014.web.controller;

import java.util.List;

import at.tuwien.aic2014.gr3.domain.TwitterUser;

public interface GUIController {
	
	public List<TwitterUser> getAllUser();
	public TwitterUser getUserById(long id);
	public TwitterUser getUserByName(String name);
	public List<String> getExistingInterestsForUser(long userId);
	public List<String> getPotetialInterestsForUser(long userId);
	public List<String> getAdsForUserExistingInterests(long userId);
	public List<String> getAdsForUserPotentiaPotentialInterests(long userId);
	

}
