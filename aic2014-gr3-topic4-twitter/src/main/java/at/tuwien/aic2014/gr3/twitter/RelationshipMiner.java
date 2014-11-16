package at.tuwien.aic2014.gr3.twitter;

import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;

import org.neo4j.unsafe.impl.batchimport.cache.NextFieldManipulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;

import at.tuwien.aic2014.gr3.dao.Neo4jTwitterUserDao;
import at.tuwien.aic2014.gr3.dao.TwitterUserDao;
import at.tuwien.aic2014.gr3.dao.TwitterUserRelationshipHandler;
import at.tuwien.aic2014.gr3.dao.TwitterUserRelationships;
import at.tuwien.aic2014.gr3.domain.TwitterUser;
import at.tuwien.aic2014.gr3.sql.DummySQLTwitter;
import at.tuwien.aic2014.gr3.sql.SQLTwitterDao;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.UserMentionEntity;

@Service
public class RelationshipMiner {

	@Autowired
	private TwitterFactory twitterFactory;
	
	private Twitter twitter;
	
    private Neo4jTwitterUserDao neo4jTwitterUserDao;
	
    public void setNeo4jTwitterUserDao(Neo4jTwitterUserDao neo4jTwitterUserDao){
    	this.neo4jTwitterUserDao = neo4jTwitterUserDao;
    }
	
	public void start(){
		twitter = twitterFactory.getInstance();
	}
	
	public long[] getFriends(long userId) throws TwitterException{
		return twitter.getFriendsIDs(userId,-1).getIDs();
	}
	
	public long[] getFollower(long userId) throws TwitterException{
		return twitter.getFollowersIDs(userId,-1).getIDs();
	}
	
	public long[] getRepliedTo(long userId) throws TwitterException{
		ArrayList<Long> repliedTo = new ArrayList<Long>();
		for(Status status :twitter.getRetweetsOfMe()){
			repliedTo.add(status.getInReplyToUserId());
		}
		return parseToArray(repliedTo);
	}
	
	private long[] parseToArray(ArrayList<Long> liste){
		int size = liste.size();
		long[] return_array = new long[size];
		for (int i = 0; i < size; i++)
        {
			return_array[i] = liste.get(i).longValue();
        }
		return return_array;
	}
	
	public long[] getMentioned(long userId) throws TwitterException{
		ArrayList<Long> mentioned = new ArrayList<Long>();
		for(Status status : twitter.getUserTimeline(userId)){
			for(UserMentionEntity ume : status.getUserMentionEntities()){
				mentioned.add(ume.getId());
			}
		}
		return parseToArray(mentioned);
	}
	
	public long[] getRetweeted(long userId) throws TwitterException{
		ArrayList<Long> retweeted = new ArrayList<Long>();
		for(Status status : twitter.getUserTimeline(userId)){
			long currentUserRetweedId = status.getCurrentUserRetweetId();
			if(currentUserRetweedId != -1L){
				retweeted.add(currentUserRetweedId);
			}
		}
		return parseToArray(retweeted);
	}
	
	/**
	 * 
	 * @param userId
	 * @param depth, 1 = friends, 2 = friends+friends of friends
	 * @return
	 * @throws TwitterException 
	 */
//	public long[] getFriendsAndFriendsOfFriends(long userId, int depth) throws TwitterException{
//		long[] friends = getFriends(userId);
//		for(long friend : friends){
//			
//		}
//	}
	
	public void crawlRealationships() throws TwitterException{
	SQLTwitterDao sqlTwitterDao = new DummySQLTwitter(); // Ersetzten mit echtem!
	List<TwitterUser> allUsers = sqlTwitterDao.findAll();
	for(TwitterUser user : allUsers){
		System.out.println(user.getId());
		crawlRelationship(user,2);
	}
	}
	
	public void crawlRelationship(TwitterUser user, int depth) throws TwitterException{
		long[] friends = getFriends(user.getId());
		long[] follower = getFollower(user.getId());
		long[] repliedTo = getRepliedTo(user.getId());
		long[] mentioned = getMentioned(user.getId());
		long[] retweeted = getRetweeted(user.getId());
		System.out.println("Friends: "); 
		printListr(friends);
		System.out.println("Follower: ");
		System.out.println("Count: " + follower.length);
		printListr(follower);
		System.out.println("Replied: ");
		printListr(repliedTo);
		System.out.println("Mentioned: ");
		printListr(mentioned);
		System.out.println("Retweeted: ");
		printListr(retweeted);
		//Save in Neo4J
		saveRelations(user, friends, TwitterUserRelationships.IS_FRIEND_OF);
		saveRelations(user, follower, TwitterUserRelationships.FOLLOWS);
		saveRelations(user, repliedTo, TwitterUserRelationships.REPLIED_TO);
		saveRelations(user, retweeted, TwitterUserRelationships.RETWEETED);
		saveRelations(user, mentioned, TwitterUserRelationships.MENTIONED);
		if(depth<=1){
			return;
		}else{
			for(long friendId : friends){
				//TODO: neo4jTwitterUserDao.find()
				TwitterUser frienduser = new TwitterUser();
				frienduser.setId(friendId);
				neo4jTwitterUserDao.create(frienduser);
				crawlRelationship(frienduser, depth-1);
			}
			for(long friendId : follower){
				//TODO: neo4jTwitterUserDao.find()
				TwitterUser frienduser = new TwitterUser();
				frienduser.setId(friendId);
				crawlRelationship(frienduser, depth-1);
			}
			for(long friendId : repliedTo){
				//TODO: neo4jTwitterUserDao.find()
				TwitterUser frienduser = new TwitterUser();
				frienduser.setId(friendId);
				crawlRelationship(frienduser, depth-1);
			}
			for(long friendId : mentioned){
				//TODO: neo4jTwitterUserDao.find()
				TwitterUser frienduser = new TwitterUser();
				frienduser.setId(friendId);
				crawlRelationship(frienduser, depth-1);
			}
			for(long friendId : retweeted){
				//TODO: neo4jTwitterUserDao.find()
				TwitterUser frienduser = new TwitterUser();
				frienduser.setId(friendId);
				crawlRelationship(frienduser, depth-1);
			}
		}
	}
	
	private void printListr(long[] list){
		for(long l : list)
			System.out.println(l);
		System.out.println("################################");
	}
	
	private void saveRelations(TwitterUser user, long[] list, TwitterUserRelationships rel){
		for(long l : list){
			TwitterUser user_rel = new TwitterUser();
			user_rel.setId(l);
			neo4jTwitterUserDao.create(user_rel); // TODO createOrUpdate()!!!
			addRel(user, user_rel,rel);
		}
	}
	
	private void addRel(TwitterUser from , TwitterUser to, TwitterUserRelationships rel){
		TwitterUserRelationshipHandler relationship = neo4jTwitterUserDao.user(from);
		switch (rel) {
		case FOLLOWS:
			relationship.follows(to);
			break;
		case IS_FRIEND_OF:
			relationship.isFriendOf(to);
			break;
		case MENTIONED:
			relationship.mentioned(to);
			break;
		case RETWEETED:
			relationship.retweeted(to);
			break;
		case REPLIED_TO:
			relationship.repliedTo(to);
			break;
		default:
			break;
		}
	}
	
	public static void main(String[] args) throws TwitterException {
//		long userId  = -1;
//		try{
//			userId = Long.parseLong(args[0]);
//		}catch(ArrayIndexOutOfBoundsException e){
//			System.err.println("userID in main als parameter fehlt!");
//			throw e;
//		}catch(NumberFormatException e){
//			throw e;
//			System.err.println("userID in main muss long sein!");
//		}
		System.setProperty("twitter4j.jsonStoreEnabled", "true");
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(TwitterConfig.class);
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("tweetsMinerContext.xml");
		Neo4jTwitterUserDao neo4jTwitterUserDao = (Neo4jTwitterUserDao) applicationContext.getBean("neo4jTwitterUserDao");
		RelationshipMiner m = ctx.getBean(RelationshipMiner.class);	
		m.setNeo4jTwitterUserDao(neo4jTwitterUserDao);
		m.start();
//		long[] ids = m.getFriends(userId);
//		for( long id : ids){
//			System.out.println(id);
//		}
		m.crawlRealationships();
		
	}
	
	
}
