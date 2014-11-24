package at.tuwien.aic2014.gr3.tweetsminer;

import at.tuwien.aic2014.gr3.dao.Neo4jTwitterUserDao;
import at.tuwien.aic2014.gr3.domain.TwitterUser;
import at.tuwien.aic2014.gr3.shared.TwitterStatusProcessor;
import at.tuwien.aic2014.gr3.tweetsminer.filters.DataCarrier;
import at.tuwien.aic2014.gr3.tweetsminer.filters.TweetFilter;
import org.apache.log4j.Logger;
import twitter4j.Status;

public class TopicExtractionTwitterStatusProcessor implements TwitterStatusProcessor {

    private static final Logger log = Logger.getLogger(TopicExtractionTwitterStatusProcessor.class);

    private Neo4jTwitterUserDao twitterUserDao;

    private TweetFilter<String[],String> tweetFilterChain;

    public void setTwitterUserDao(Neo4jTwitterUserDao twitterUserDao) {
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

            twitterUserDao.user(tweetAuthor).mentionedTopic(topic);
        }

        log.debug("Topic extraction successfully processed.");
    }
}
