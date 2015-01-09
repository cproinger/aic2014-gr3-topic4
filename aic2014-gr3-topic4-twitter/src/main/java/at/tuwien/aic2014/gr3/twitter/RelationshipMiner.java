package at.tuwien.aic2014.gr3.twitter;

import at.tuwien.aic2014.gr3.domain.TwitterUser;
import at.tuwien.aic2014.gr3.graphdb.Neo4jTwitterUserRepository;
import at.tuwien.aic2014.gr3.graphdb.TwitterUserRelationshipHandler;
import at.tuwien.aic2014.gr3.graphdb.TwitterUserRelationships;
import at.tuwien.aic2014.gr3.shared.RepositoryException;
import at.tuwien.aic2014.gr3.shared.RepositoryIterator;
import at.tuwien.aic2014.gr3.sql.SqlUserRepository;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.UserMentionEntity;

import java.util.ArrayList;
import java.util.Date;

@Service
public class RelationshipMiner {

    private final static Logger log = Logger.getLogger(RelationshipMiner.class);

    private static Thread mainThread = null;
    private static boolean running = false;

	private Twitter twitter;
	
    private Neo4jTwitterUserRepository neo4jTwitterUserDao;

    private SqlUserRepository sqlUserRepository;
	
    public void setNeo4jTwitterUserDao(Neo4jTwitterUserRepository neo4jTwitterUserDao){
    	this.neo4jTwitterUserDao = neo4jTwitterUserDao;
    }

    public void setSqlUserRepository(SqlUserRepository sqlUserRepository) {
        this.sqlUserRepository = sqlUserRepository;
    }

    public void setTwitter(Twitter twitter) {
        this.twitter = twitter;
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
	
	public void crawlRealationships() {
        log.info("Relationships Miner started crawling");

        while (running) {
            try {
                RepositoryIterator<TwitterUser> it = sqlUserRepository.readAll();

                while (running && it.hasNext()) {
                    TwitterUser user = it.next();
                    log.debug("Crawling relationships for user " + user.getId());

                    try {
                        crawlRelationship(user, 2);
                    } catch (TwitterException e) {
                        log.error("Crawling error reported", e);
                    }
                }
            } catch (RepositoryException e) {
                log.error("Something terrible happened", e);
            }
        }

        log.info("Relationships Miner successfully terminated!");
	}
	
	public void crawlRelationship(TwitterUser user, int depth) throws TwitterException{
        if (alreadySynched (user)) {
            return;
        }

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
        if (!running) {
            return;
        }
		saveRelations(user, friends, TwitterUserRelationships.IS_FRIEND_OF);
		saveRelations(user, follower, TwitterUserRelationships.FOLLOWS);
		saveRelations(user, repliedTo, TwitterUserRelationships.REPLIED_TO);
		saveRelations(user, retweeted, TwitterUserRelationships.RETWEETED);
		saveRelations(user, mentioned, TwitterUserRelationships.MENTIONED);

        user.setLastTimeSynched(new Date());
        try {
            sqlUserRepository.save(user);
        } catch (RepositoryException e) {
            log.error("SQL exception reported", e);
        }

        if(depth<=1){
			return;
		}else{
			for(long friendId : friends){
				//TODO: neo4jTwitterUserDao.find()
				TwitterUser frienduser = new TwitterUser();
				frienduser.setId(friendId);
				neo4jTwitterUserDao.save(frienduser);
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

    private boolean alreadySynched (TwitterUser user) {
        return user.getLastTimeSynched() != null; //synchronise only once in current version
    }
	
	private void printListr(long[] list){
		for(long l : list)
			System.out.println(l);
		System.out.println("################################");
	}
	
	private void saveRelations(TwitterUser user, long[] list, TwitterUserRelationships rel){
        if (!running) {
            return;
        }
		for(long l : list){
			TwitterUser user_rel = new TwitterUser();
			user_rel.setId(l);
			neo4jTwitterUserDao.save(user_rel);
			addRel(user, user_rel,rel);
		}
	}
	
	private void addRel(TwitterUser from , TwitterUser to, TwitterUserRelationships rel){
		TwitterUserRelationshipHandler relationship = neo4jTwitterUserDao.relation(from);
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
        log.info("-------- Relationships Miner App Starting --------");

		System.setProperty("twitter4j.jsonStoreEnabled", "true");
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("relationshipMinerContext.xml");

		RelationshipMiner m = applicationContext.getBean("relationshipMiner", RelationshipMiner.class);

        running = true;
        mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.info("Shutting Relationships Miner down...");
                running = false;
                try {
                    mainThread.join();
                } catch (InterruptedException e) {
                    /* Nothing to be done, the app will be closed */
                }
            }
        });

		m.crawlRealationships();
	}
}
