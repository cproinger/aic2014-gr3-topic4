package at.tuwien.aic2014.gr3.sql;

import at.tuwien.aic2014.gr3.docstore.DocStoreConfig;
import at.tuwien.aic2014.gr3.domain.TwitterUser;
import at.tuwien.aic2014.gr3.domain.TwitterUserUtils;
import at.tuwien.aic2014.gr3.shared.TweetProcessing;
import at.tuwien.aic2014.gr3.shared.TweetRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import twitter4j.Status;
import twitter4j.User;

import javax.sql.DataSource;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: putzchri
 * Date: 02.11.14
 * Time: 12:59
 * To change this template use File | Settings | File Templates.
 */
@Service
public class SQLTweetProcessing implements TweetProcessing {
	
    private TweetRepository tweetRepo;
    private SqlUserRepository userRepo;
    
    @Autowired
    private DataSource dataSource;

    @Autowired
    public SQLTweetProcessing(TweetRepository tweetRepo, SqlUserRepository userRepo) {
    	this.tweetRepo = tweetRepo;
    	this.userRepo = userRepo;
    }
    

	private void processStatus(Status stat) {
		TwitterUserUtils utils = new TwitterUserUtils();
		User user = stat.getUser();
		//scheinbar gibts welche ohne user???
		if(user == null)
			return;
		TwitterUser tu = utils.create(user);
		userRepo.save(tu);
	}

	public void processAll() {
		for(Iterator<Status> it = tweetRepo.iterateTweetsWithUnprocessedUser(); it.hasNext(); ) {
			Status stat = it.next();
			System.out.println("process tweet " +stat.getId());
			processStatus(stat);
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
