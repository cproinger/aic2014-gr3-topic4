package at.tuwien.aic2014.gr3.twitter;

import at.tuwien.aic2014.gr3.domain.TwitterUser;
import at.tuwien.aic2014.gr3.domain.TwitterUserUtils;
import at.tuwien.aic2014.gr3.graphdb.Neo4jTwitterUserRepository;
import at.tuwien.aic2014.gr3.graphdb.TwitterUserRelationshipHandler;
import at.tuwien.aic2014.gr3.graphdb.TwitterUserRelationships;
import at.tuwien.aic2014.gr3.shared.RepositoryException;
import at.tuwien.aic2014.gr3.shared.RepositoryIterator;
import at.tuwien.aic2014.gr3.shared.TwitterStatusProcessor;
import at.tuwien.aic2014.gr3.sql.SqlUserRepository;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import twitter4j.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

@Service
public class RelationshipMiner {

    private final static Logger log = Logger.getLogger(RelationshipMiner.class);

    private static Thread mainThread = null;
    private static boolean running = false;

	private Twitter twitter;
    private TwitterStatusProcessor statusProcessor;
	
    private Neo4jTwitterUserRepository neo4jTwitterUserDao;

    private SqlUserRepository sqlUserRepository;
	
    public void setNeo4jTwitterUserDao(Neo4jTwitterUserRepository neo4jTwitterUserDao){
    	this.neo4jTwitterUserDao = neo4jTwitterUserDao;
    }

    public void setStatusProcessor(TwitterStatusProcessor statusProcessor) {
        this.statusProcessor = statusProcessor;
    }

    public void setSqlUserRepository(SqlUserRepository sqlUserRepository) {
        this.sqlUserRepository = sqlUserRepository;
    }

    public void setTwitter(Twitter twitter) {
        this.twitter = twitter;
    }

    public Iterator<User> getFriends(long userId) throws TwitterException{
        return twitter.getFriendsList(userId, 0).iterator();
	}
	
	public Iterator<User> getFollowers(long userId) throws TwitterException{
		return twitter.getFollowersList(userId,0).iterator();
	}
	
	public void crawlRealationships() {
        log.info("Relationships Miner started crawling");

        while (running) {
            try {
                RepositoryIterator<TwitterUser> it = sqlUserRepository.readAll();

                while (running && it.hasNext()) {
                    TwitterUser user = it.next();
                    log.debug("Crawling relationships for user " + user.getId());

                    try {
                        crawlRelationships(user);
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
	
	public void crawlRelationships(TwitterUser user) throws TwitterException{
        if (alreadySynched (user)) {
            return;
        }

		Iterator<User> friendsIterator = getFriends(user.getId());
		Iterator<User> followerIterator = getFollowers(user.getId());

        user.setLastTimeSynched(new Date());
        try {
            sqlUserRepository.save(user);
        } catch (RepositoryException e) {
            log.error("SQL exception reported", e);
        }

        while (friendsIterator.hasNext()) {
            TwitterUser friend = TwitterUserUtils.create(friendsIterator.next());
            handleFriendshipRelation (user, friend);
        }

        while (followerIterator.hasNext()) {
            TwitterUser follower = TwitterUserUtils.create(followerIterator.next());
            handleFriendshipRelation(follower, user);
        }
	}

    private void handleFriendshipRelation(TwitterUser user, TwitterUser friend) {
        try {
            sqlUserRepository.save(friend);
            neo4jTwitterUserDao.relation(user).isFriendOf(friend);
        } catch (Exception e) {
            log.error ("Repository error", e);
        }

        try {
            ResponseList<Status> statuses = twitter.getUserTimeline(friend.getId());

            for (Status status : statuses) {
                statusProcessor.process(status);
            }
        } catch (Exception e) {
            log.error("Twitter error", e);
        }
    }

    private boolean alreadySynched (TwitterUser user) {
        return user.getLastTimeSynched() != null; //synchronise only once in current version
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
