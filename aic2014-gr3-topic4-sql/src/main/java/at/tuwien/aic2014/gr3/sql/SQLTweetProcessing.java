package at.tuwien.aic2014.gr3.sql;

import at.tuwien.aic2014.gr3.docstore.DocStoreConfig;
import at.tuwien.aic2014.gr3.domain.TwitterUser;
import at.tuwien.aic2014.gr3.domain.TwitterUserUtils;
import at.tuwien.aic2014.gr3.shared.RepositoryException;
import at.tuwien.aic2014.gr3.shared.TweetRepository;
import at.tuwien.aic2014.gr3.shared.TwitterStatusProcessor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;
import twitter4j.Status;
import twitter4j.User;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: putzchri
 * Date: 02.11.14
 * Time: 12:59
 * To change this template use File | Settings | File Templates.
 */
@Service
public class SQLTweetProcessing implements TwitterStatusProcessor {
	
    private TweetRepository tweetRepo;
    private SqlUserRepository userRepo;

    private static final Logger log = Logger.getLogger(SQLTweetProcessing.class);
    
    @Autowired
    public SQLTweetProcessing(TweetRepository tweetRepo, SqlUserRepository userRepo) {
    	this.tweetRepo = tweetRepo;
    	this.userRepo = userRepo;
    }
    
    @Override
	public void process(Status twitterStatus) {
		User user = twitterStatus.getUser();
		//scheinbar gibts welche ohne user???
		if(user == null)
			return;
		TwitterUser tu = TwitterUserUtils.create(user);
        try {
            userRepo.save(tu);
        } catch (RepositoryException e) {
            log.error("Error while saving user " + tu.getId(), e);
        }
    }

	public void processAll() {
		for(Iterator<Status> it = tweetRepo.iterateTweetsWithUnprocessedUser(); it.hasNext(); ) {
			Status stat = it.next();
			System.out.println("process tweet " +stat.getId());
			process(stat);
			it.remove();
		}
	}

	public static void main(String[] args) {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
				SqlConfig.class, DocStoreConfig.class);
		SQLTweetProcessing m = ctx.getBean(SQLTweetProcessing.class);
		m.processAll();
		ctx.close();	
	}
}
