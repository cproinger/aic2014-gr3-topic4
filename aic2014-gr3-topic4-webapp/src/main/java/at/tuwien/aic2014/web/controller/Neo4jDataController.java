package at.tuwien.aic2014.web.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;

import at.tuwien.aic2014.gr3.domain.Advertisment;
import at.tuwien.aic2014.gr3.domain.InterestedUsers;
import at.tuwien.aic2014.gr3.domain.InterestedUsersResult;
import at.tuwien.aic2014.gr3.domain.PotentialInterest;
import at.tuwien.aic2014.gr3.domain.TwitterUser;
import at.tuwien.aic2014.gr3.domain.UserAndCount;
import at.tuwien.aic2014.gr3.domain.UserTopic;
import at.tuwien.aic2014.gr3.graphdb.Neo4jTwitterUserRepository;
import at.tuwien.aic2014.gr3.shared.AdvertismentRepository;

@Component
public class Neo4jDataController implements GUIController {
	
	@Autowired
	private Neo4jTwitterUserRepository neo4jTwitterUserDao;
//	
	@Autowired
	private AdvertismentRepository adRepository;
	
//	@Autowired
//	private SqlUserRepository;
	 
//	 private ApplicationContext applicationContext = new ClassPathXmlApplicationContext("guiContext.xml");
	 
	@Override
	public List<TwitterUser> getInterstedUsers(boolean ascending, int processedCountMoreThan, int maxResults) {
			List<TwitterUser> allUser = new ArrayList<TwitterUser>();
//			InterestedUsers users =neo4jTwitterUserDao.findInterstedUsers(true, 10, 10);
			List<InterestedUsersResult> users = neo4jTwitterUserDao.findInterstedUsers(ascending,processedCountMoreThan,maxResults);
			for(InterestedUsersResult iur : users){
				allUser.add(iur.getUser());
			}
		return allUser;
	}

	@Override
	public TwitterUser getUserById(long id) {
//		if(allUser_cache==null){
//			getAllUser();
//		}
		return null;
//		return allUser_cache.get(Long.valueOf(id));
	}

	@Override
	public TwitterUser getUserByName(String name) {
//		getAllUser();
//		return allUser_name_chache.get(name);
		return null;
	}

	@Override
	public List<UserTopic> getExistingInterestsForUser(long userId) {
		return neo4jTwitterUserDao.findExistingInterestsForUser(userId);
	}

	@Override
	public List<PotentialInterest> getPotetialInterestsForUser(long userId) {
		return	neo4jTwitterUserDao.findPotentialInterestsForUser(userId, 2, 5);
	}

	@Override
	public Collection<Advertisment> getAdsForUserExistingInterests(long userId) {
		List<UserTopic> topics = neo4jTwitterUserDao.findExistingInterestsForUser(userId);
		return adRepository.findByInterests(topics, 10);
	}

	@Override
	public Collection<Advertisment> getAdsForUserPotentiaPotentialInterests(long userId) {
		List<PotentialInterest> topics = neo4jTwitterUserDao.findPotentialInterestsForUser(userId, 2, 5);
		return adRepository.findByInterests(topics, 10);
	}

}
