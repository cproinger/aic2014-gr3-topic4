package at.tuwien.aic2014.gr3.twitter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

@Service
public class RelationshipMiner {

	@Autowired
	private TwitterFactory twitterFactory;
	
	private Twitter twitter;
	
	public void start(){
		twitter = twitterFactory.getInstance();
	}
	
	
	public long[] getFriends(long userId) throws TwitterException{
		return twitter.getFriendsIDs(userId, -1).getIDs();
	}
	
	public static void main(String[] args) throws TwitterException {
		long userId  = -1;
		try{
			userId = Long.parseLong(args[0]);
		}catch(ArrayIndexOutOfBoundsException e){
			System.err.println("userID in main als parameter fehlt!");
			throw e;
		}catch(NumberFormatException e){
			System.err.println("userID in main muss long sein!");
			throw e;
		}
		System.setProperty("twitter4j.jsonStoreEnabled", "true");
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(TwitterConfig.class);
		RelationshipMiner m = ctx.getBean(RelationshipMiner.class);	
		m.start();
		long[] ids = m.getFriends(userId);
		for( long id : ids){
			System.out.println(id);
		}
	}
	
	
}
