package at.tuwien.aic2014.gr3.tweetsminer;

import at.tuwien.aic2014.gr3.dao.Neo4jTwitterUserDao;
import at.tuwien.aic2014.gr3.dao.Neo4jTwitterUserRelationshipHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import twitter4j.Status;
import twitter4j.TwitterObjectFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:tweetsMinerTestContext.xml")
public abstract class TweetProcessorTest {

    private static final String dropAllQuery =
            "MATCH (n) " +
            "OPTIONAL MATCH (n)-[r]-() " +
            "DELETE n,r";


    @Autowired
    private GraphDatabaseService graphdb;
    @Autowired
    private ExecutionEngine engine;

    @Value("classpath:testingTwitterStatus.json")
    private Resource testingTweetResource;

    private Status testingTwitterStatus;

    @Before
    public void setUp() throws Exception {
        String jsonString = "";
        Scanner sc = new Scanner(testingTweetResource.getFile());
        while (sc.hasNextLine())
            jsonString += sc.nextLine();
        sc.close();

        testingTwitterStatus = TwitterObjectFactory.createStatus(jsonString);
    }

    @After
    public void tearDown() throws Exception {
        Transaction tx = graphdb.beginTx();

        engine.execute(dropAllQuery);

        tx.success();
    }

    Status getTestingTwitterStatus() {
        return testingTwitterStatus;
    }

    void assertUserRelationship(long u1Id, String relationship, long u2Id, int degree) {
        assertUserUniquePresence(u1Id);
        assertUserUniquePresence(u2Id);

        String query = String.format(
                "MATCH (u1:%s {%s:{u1_id}}) - [rel:%s] -> (u2:%s {%s:{u2_id}}) RETURN rel",
                Neo4jTwitterUserDao.TWITTER_USER_NODE_LABEL.name(),
                Neo4jTwitterUserDao.TWITTER_USER_ID_PROP,
                relationship,
                Neo4jTwitterUserDao.TWITTER_USER_NODE_LABEL.name(),
                Neo4jTwitterUserDao.TWITTER_USER_ID_PROP);
        Map<String, Object> params = new HashMap<>();
        params.put("u1_id", u1Id);
        params.put("u2_id", u2Id);

        ExecutionResult res = engine.execute(query, params);

        assertEquals(degree, res.columnAs("rel").length());

        res.close();
    }

    void assertUserRelationshipHashtag(long uId, String hashtag, int degree) {
        assertUserUniquePresence(uId);
        assertHashtagUniquePresence(hashtag);

        String query = String.format(
                "MATCH (u1:%s {%s:{u1_id}}) - [rel:%s] -> (hashtag:%s {%s:{hashtag}}) RETURN rel",
                Neo4jTwitterUserDao.TWITTER_USER_NODE_LABEL.name(),
                Neo4jTwitterUserDao.TWITTER_USER_ID_PROP,
                "MENTIONED_HASHTAG",
                Neo4jTwitterUserRelationshipHandler.HASHTAG_LABEL.name(),
                Neo4jTwitterUserRelationshipHandler.HASHTAG_NAME_PROP);
        Map<String, Object> params = new HashMap<>();
        params.put("u1_id", uId);
        params.put("hashtag", hashtag);

        ExecutionResult res = engine.execute(query, params);

        assertEquals(degree, res.columnAs("rel").length());

        res.close();
    }

    void assertUserRelationshipTopic(long uId, String topic, int degree) {
        assertUserUniquePresence(uId);
        assertTopicUniquePresence(topic);

        String query = String.format(
                "MATCH (u1:%s {%s:{u1_id}}) - [rel:%s] -> (topic:%s {%s:{topic}}) RETURN rel",
                Neo4jTwitterUserDao.TWITTER_USER_NODE_LABEL.name(),
                Neo4jTwitterUserDao.TWITTER_USER_ID_PROP,
                "MENTIONED_TOPIC",
                Neo4jTwitterUserRelationshipHandler.TOPIC_LABEL.name(),
                Neo4jTwitterUserRelationshipHandler.TOPIC_NAME_PROP);
        Map<String, Object> params = new HashMap<>();
        params.put("u1_id", uId);
        params.put("topic", topic);

        ExecutionResult res = engine.execute(query, params);

        assertEquals(degree, res.columnAs("rel").length());

        res.close();
    }

    void assertHashtagUniquePresence(String hashtag) {
        String query =
                "MATCH (n:" + Neo4jTwitterUserRelationshipHandler.HASHTAG_LABEL.name() + ") " +
                "WHERE n." + Neo4jTwitterUserRelationshipHandler.HASHTAG_NAME_PROP + " = {hashtag} " +
                "RETURN n";
        Map<String, Object> params = new HashMap<>();
        params.put("hashtag", hashtag);
        ExecutionResult res = engine.execute(query, params);

        assertEquals(1, res.columnAs("n").length());

        res.close();
    }

    void assertTopicUniquePresence(String topic) {
        String query =
                "MATCH (n:" + Neo4jTwitterUserRelationshipHandler.TOPIC_LABEL.name() + ") " +
                "WHERE n." + Neo4jTwitterUserRelationshipHandler.TOPIC_NAME_PROP+ " = {topic} " +
                "RETURN n";
        Map<String, Object> params = new HashMap<>();
        params.put("topic", topic);
        ExecutionResult res = engine.execute(query, params);

        assertEquals(1, res.columnAs("n").length());

        res.close();
    }

    void assertUserUniquePresence(long userId) {
        String query =
                "MATCH (n:" + Neo4jTwitterUserDao.TWITTER_USER_NODE_LABEL.name() + ") " +
                "WHERE n." + Neo4jTwitterUserDao.TWITTER_USER_ID_PROP + " = {id} " +
                "RETURN n";
        Map<String, Object> params = new HashMap<>();
        params.put("id", userId);
        ExecutionResult res = engine.execute(query, params);

        assertEquals(1, res.columnAs("n").length());

        res.close();
    }
}