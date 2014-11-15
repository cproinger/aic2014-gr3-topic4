package at.tuwien.aic2014.gr3.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import twitter4j.Status;
import twitter4j.User;
import at.tuwien.aic2014.gr3.docstore.DocStoreConfig;
import at.tuwien.aic2014.gr3.domain.TwitterUser;
import at.tuwien.aic2014.gr3.domain.TwitterUserUtils;
import at.tuwien.aic2014.gr3.shared.TweetProcessing;
import at.tuwien.aic2014.gr3.shared.TweetRepository;

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
		//TODO replace safeTweetIntoSql with this. 
		TwitterUserUtils utils = new TwitterUserUtils();
		User user = stat.getUser();
		//scheinbar gibts welche ohne user???
		if(user == null)
			return;
		TwitterUser tu = utils.create(user);
		userRepo.save(tu);
	}

    //@Override
    @PostConstruct
    public void initializeSQLDatabase() {
    	//TODO move to SqlUserRepository. 
        try(Connection conn = dataSource.getConnection()) {
            Statement stat = conn.createStatement();
            stat.execute("create table test(id bigint primary key, name varchar(255),screen_name varchar(255)," +
                    "location varchar(255), description varchar(255),protected boolean," +
                    "verified boolean,followers_count integer,friends_count integer, listed_count integer," +
                    " favourites_count integer, created_at datetime,language varchar(255)," +
                    "last_time_synched datetime)");

            stat.execute("insert into test values(1,'Hello','','','http://t.co/wBsL7knD3R','',false,false,0,10,0,0,null,'ru',null)");
            stat.execute("insert into test values(2,'Test','','','http://t.co/wBsL7knD3R','',false,false,0,10,0,0,null,'ru',null)");
            ResultSet rs = stat.executeQuery("select * from test");
            while (rs.next()) {
                System.out.println(rs.getString("name"));
            }
            stat.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

	public void processAll() {
		for(Iterator<Status> it = tweetRepo.iterateTweetsWithUnprocessedUser(); it.hasNext(); ) {
			Status stat = it.next();
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
