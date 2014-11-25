package at.tuwien.aic2014.gr3.tweetsminer;

import at.tuwien.aic2014.gr3.graphdb.Neo4jTwitterUserRepository;
import at.tuwien.aic2014.gr3.domain.TwitterUser;
import at.tuwien.aic2014.gr3.shared.TwitterStatusProcessor;
import at.tuwien.aic2014.gr3.tweetsminer.filters.DataCarrier;
import at.tuwien.aic2014.gr3.tweetsminer.filters.TweetFilter;
import org.apache.log4j.Logger;
import twitter4j.Status;

public class TopicExtractionTwitterStatusProcessor implements TwitterStatusProcessor {

    private static final Logger log = Logger.getLogger(TopicExtractionTwitterStatusProcessor.class);

    private Neo4jTwitterUserRepository twitterUserDao;

    private TweetFilter<String[],String> tweetFilterChain;

    public void setTwitterUserDao(Neo4jTwitterUserRepository twitterUserDao) {
        this.twitterUserDao = twitterUserDao;
    }

    public void setTweetFilterChain(TweetFilter<String[], String> tweetFilterChain) {
        this.tweetFilterChain = tweetFilterChain;
    }

    @Override
    public void process(Status twitterStatus) {
        log.debug("Processing topic extraction from tweet...");

        TwitterUser tweetAuthor = new TwitterUser();
        tweetAuthor.setId(twitterStatus.getUser().getId());

        String[] processedTopics = tweetFilterChain.filter(
                new DataCarrier<>(twitterStatus.getText())).getData();

        for (String topic : processedTopics) {
            log.debug("Topic extracted from tweet: " + topic);

            twitterUserDao.relation(tweetAuthor).mentionedTopic(topic);
        }

        log.debug("Topic extraction successfully processed.");
    }
}
