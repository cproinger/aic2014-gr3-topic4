package at.tuwien.aic2014.gr3.dao;

import at.tuwien.aic2014.gr3.domain.TwitterUser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:tweetsMinerTestContext.xml")
public class Neo4jTwitterUserDaoTest {

    private static final String dropAllQuery =
            "MATCH (n) " +
            "OPTIONAL MATCH (n)-[r]-() " +
            "DELETE n,r";

    @Autowired
    private GraphDatabaseService graphdb;

    @Autowired
    private ExecutionEngine engine;

    @Autowired
    private Neo4jTwitterUserDao neo4jTwitterUserDao;

    @Autowired
    private TwitterUser testTwitterUser;

    @Before
    public void setUp() throws Exception {
        Transaction tx = graphdb.beginTx();

        Node testUserNode = graphdb.createNode(Neo4jTwitterUserDao.TWITTER_USER_NODE_LABEL);
        testUserNode.setProperty(Neo4jTwitterUserDao.TWITTER_USER_ID_PROP, testTwitterUser.getId());


        tx.success();
    }

    @After
    public void tearDown() throws Exception {
        Transaction tx = graphdb.beginTx();

        engine.execute(dropAllQuery);

        tx.success();
    }

    @Test
    public void testCreate() throws Exception {
        TwitterUser user = new TwitterUser();
        user.setId(2);

        neo4jTwitterUserDao.create(user);

        assertUserUniquePresence(user);
    }

    @Test
    public void testReadById() throws Exception {
        assertNotNull(neo4jTwitterUserDao.readById(testTwitterUser.getId()));
    }

    @Test
    public void testFollowsRelationshipSingleWithExistentUser() throws Exception {
        neo4jTwitterUserDao.user(testTwitterUser).follows(testTwitterUser);

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.FOLLOWS.name(), testTwitterUser, 1);
    }

    @Test
    public void testFollowsRelationshipSingleWithNewUser() throws Exception {
        TwitterUser newUser = new TwitterUser();
        newUser.setId(testTwitterUser.getId() + 1);

        neo4jTwitterUserDao.user(testTwitterUser).follows(newUser);

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.FOLLOWS.name(), newUser, 1);
    }

    @Test
    public void testFollowsRelationshipMultiple() throws Exception {
        TwitterUser newUser = new TwitterUser();
        newUser.setId(testTwitterUser.getId() + 1);

        int degree = 10;
        for (int i = 0; i < degree; ++i) {
            neo4jTwitterUserDao.user(testTwitterUser).follows(newUser);
        }

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.FOLLOWS.name(), newUser, degree);
    }

    @Test
    public void testMentionedRelationshipSingleWithExistentUser() throws Exception {
        neo4jTwitterUserDao.user(testTwitterUser).mentioned(testTwitterUser);

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.MENTIONED.name(), testTwitterUser, 1);
    }

    @Test
    public void testMentionedRelationshipSingleWithNewUser() throws Exception {
        TwitterUser newUser = new TwitterUser();
        newUser.setId(testTwitterUser.getId() + 1);

        neo4jTwitterUserDao.user(testTwitterUser).mentioned(newUser);

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.MENTIONED.name(), newUser, 1);
    }

    @Test
    public void testMentionedRelationshipMultiple() throws Exception {
        TwitterUser newUser = new TwitterUser();
        newUser.setId(testTwitterUser.getId() + 1);

        int degree = 10;
        for (int i = 0; i < degree; ++i) {
            neo4jTwitterUserDao.user(testTwitterUser).mentioned(newUser);
        }

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.MENTIONED.name(), newUser, degree);
    }

    @Test
    public void testRetweetedRelationshipSingleWithExistentUser() throws Exception {
        neo4jTwitterUserDao.user(testTwitterUser).retweeted(testTwitterUser);

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.RETWEETED.name(), testTwitterUser, 1);
    }

    @Test
    public void testRetweetedRelationshipSingleWithNewUser() throws Exception {
        TwitterUser newUser = new TwitterUser();
        newUser.setId(testTwitterUser.getId() + 1);

        neo4jTwitterUserDao.user(testTwitterUser).retweeted(newUser);

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.RETWEETED.name(), newUser, 1);
    }

    @Test
    public void testRetweetedRelationshipMultiple() throws Exception {
        TwitterUser newUser = new TwitterUser();
        newUser.setId(testTwitterUser.getId() + 1);

        int degree = 10;
        for (int i = 0; i < degree; ++i) {
            neo4jTwitterUserDao.user(testTwitterUser).retweeted(newUser);
        }

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.RETWEETED.name(), newUser, degree);
    }

    @Test
    public void testIsFriendOfRelationshipSingleWithExistentUser() throws Exception {
        neo4jTwitterUserDao.user(testTwitterUser).isFriendOf(testTwitterUser);

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.IS_FRIEND_OF.name(), testTwitterUser, 1);
    }

    @Test
    public void testIsFriendOfRelationshipSingleWithNewUser() throws Exception {
        TwitterUser newUser = new TwitterUser();
        newUser.setId(testTwitterUser.getId() + 1);

        neo4jTwitterUserDao.user(testTwitterUser).isFriendOf(newUser);

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.IS_FRIEND_OF.name(), newUser, 1);
    }

    @Test
    public void testIsFriendOfRelationshipMultiple() throws Exception {
        TwitterUser newUser = new TwitterUser();
        newUser.setId(testTwitterUser.getId() + 1);

        int degree = 10;
        for (int i = 0; i < degree; ++i) {
            neo4jTwitterUserDao.user(testTwitterUser).isFriendOf(newUser);
        }

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.IS_FRIEND_OF.name(), newUser, degree);
    }

    @Test
    public void testRepliedToRelationshipSingleWithExistentUser() throws Exception {
        neo4jTwitterUserDao.user(testTwitterUser).repliedTo(testTwitterUser);

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.REPLIED_TO.name(), testTwitterUser, 1);
    }

    @Test
    public void testRepliedToRelationshipSingleWithNewUser() throws Exception {
        TwitterUser newUser = new TwitterUser();
        newUser.setId(testTwitterUser.getId() + 1);

        neo4jTwitterUserDao.user(testTwitterUser).repliedTo(newUser);

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.REPLIED_TO.name(), newUser, 1);
    }

    @Test
    public void testRepliedToRelationshipMultiple() throws Exception {
        TwitterUser newUser = new TwitterUser();
        newUser.setId(testTwitterUser.getId() + 1);

        int degree = 10;
        for (int i = 0; i < degree; ++i) {
            neo4jTwitterUserDao.user(testTwitterUser).repliedTo(newUser);
        }

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.REPLIED_TO.name(), newUser, degree);
    }

    @Test
    public void testMentionedHashtagRelationshipSingleWithExistentHashtag() throws Exception {
        String hashtag = "unit-testing";

        Transaction tx = graphdb.beginTx();
        Node hashtagNode = graphdb.createNode(Neo4jTwitterUserRelationshipHandler.HASHTAG_LABEL);
        hashtagNode.setProperty(Neo4jTwitterUserRelationshipHandler.HASHTAG_NAME_PROP, hashtag);
        tx.success();

        neo4jTwitterUserDao.user(testTwitterUser).mentionedHashtag(hashtag);

        assertUserRelationshipHashtag(testTwitterUser, hashtag, 1);
    }

    @Test
    public void testMentionedHashtagRelationshipSingleWithNewHashtag() throws Exception {
        String hashtag = "unit-testing";

        neo4jTwitterUserDao.user(testTwitterUser).mentionedHashtag(hashtag);

        assertUserRelationshipHashtag(testTwitterUser, hashtag, 1);
    }

    @Test
    public void testMentionedHashtagRelationshipMultiple() throws Exception {
        String hashtag = "unit-testing";

        int degree = 10;
        for (int i = 0; i < degree; ++i) {
            neo4jTwitterUserDao.user(testTwitterUser).mentionedHashtag(hashtag);
        }

        assertUserRelationshipHashtag(testTwitterUser, hashtag, degree);
    }

    @Test
    public void testMentionedTopicRelationshipSingleWithExistentTopic() throws Exception {
        String topic = "unit-testing";

        Transaction tx = graphdb.beginTx();
        Node hashtagNode = graphdb.createNode(Neo4jTwitterUserRelationshipHandler.TOPIC_LABEL);
        hashtagNode.setProperty(Neo4jTwitterUserRelationshipHandler.TOPIC_NAME_PROP, topic);
        tx.success();

        neo4jTwitterUserDao.user(testTwitterUser).mentionedTopic(topic);

        assertUserRelationshipTopic(testTwitterUser, topic, 1);
    }

    @Test
    public void testMentionedHashtagRelationshipSingleWithNewTopic() throws Exception {
        String topic = "unit-testing";

        neo4jTwitterUserDao.user(testTwitterUser).mentionedTopic(topic);

        assertUserRelationshipTopic(testTwitterUser, topic, 1);
    }

    @Test
    public void testMentionedTopicRelationshipMultiple() throws Exception {
        String topic = "unit-testing";

        int degree = 10;
        for (int i = 0; i < degree; ++i) {
            neo4jTwitterUserDao.user(testTwitterUser).mentionedTopic(topic);
        }

        assertUserRelationshipTopic(testTwitterUser, topic, degree);
    }

    private void assertUserRelationship (TwitterUser u1, String relationship, TwitterUser u2, int degree) {
        assertUserUniquePresence(u1);
        assertUserUniquePresence(u2);

        String query = String.format(
                "MATCH (u1:%s {%s:{u1_id}}) - [rel:%s] -> (u2:%s {%s:{u2_id}}) RETURN rel",
                Neo4jTwitterUserDao.TWITTER_USER_NODE_LABEL.name(),
                Neo4jTwitterUserDao.TWITTER_USER_ID_PROP,
                relationship,
                Neo4jTwitterUserDao.TWITTER_USER_NODE_LABEL.name(),
                Neo4jTwitterUserDao.TWITTER_USER_ID_PROP);
        Map<String, Object> params = new HashMap<>();
        params.put("u1_id", u1.getId());
        params.put("u2_id", u2.getId());

        ExecutionResult res = engine.execute(query, params);

        assertEquals(degree, res.columnAs("rel").length());

        res.close();
    }

    private void assertUserRelationshipHashtag (TwitterUser u1, String hashtag, int degree) {
        assertUserUniquePresence(u1);
        assertHashtagUniquePresence(hashtag);

        String query = String.format(
                "MATCH (u1:%s {%s:{u1_id}}) - [rel:%s] -> (hashtag:%s {%s:{hashtag}}) RETURN rel",
                Neo4jTwitterUserDao.TWITTER_USER_NODE_LABEL.name(),
                Neo4jTwitterUserDao.TWITTER_USER_ID_PROP,
                TwitterUserRelationships.MENTIONED_HASHTAG.name(),
                Neo4jTwitterUserRelationshipHandler.HASHTAG_LABEL.name(),
                Neo4jTwitterUserRelationshipHandler.HASHTAG_NAME_PROP);
        Map<String, Object> params = new HashMap<>();
        params.put("u1_id", u1.getId());
        params.put("hashtag", hashtag);

        ExecutionResult res = engine.execute(query, params);

        assertEquals(degree, res.columnAs("rel").length());

        res.close();
    }

    private void assertUserRelationshipTopic (TwitterUser u1, String topic, int degree) {
        assertUserUniquePresence(u1);
        assertTopicUniquePresence(topic);

        String query = String.format(
                "MATCH (u1:%s {%s:{u1_id}}) - [rel:%s] -> (topic:%s {%s:{topic}}) RETURN rel",
                Neo4jTwitterUserDao.TWITTER_USER_NODE_LABEL.name(),
                Neo4jTwitterUserDao.TWITTER_USER_ID_PROP,
                TwitterUserRelationships.MENTIONED_TOPIC.name(),
                Neo4jTwitterUserRelationshipHandler.TOPIC_LABEL.name(),
                Neo4jTwitterUserRelationshipHandler.TOPIC_NAME_PROP);
        Map<String, Object> params = new HashMap<>();
        params.put("u1_id", u1.getId());
        params.put("topic", topic);

        ExecutionResult res = engine.execute(query, params);

        assertEquals(degree, res.columnAs("rel").length());

        res.close();
    }

    private void assertHashtagUniquePresence(String hashtag) {
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

    private void assertTopicUniquePresence(String topic) {
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

    private void assertUserUniquePresence(TwitterUser user) {
        String query =
                "MATCH (n:" + Neo4jTwitterUserDao.TWITTER_USER_NODE_LABEL.name() + ") " +
                "WHERE n." + Neo4jTwitterUserDao.TWITTER_USER_ID_PROP + " = {id} " +
                "RETURN n";
        Map<String, Object> params = new HashMap<>();
        params.put("id", user.getId());
        ExecutionResult res = engine.execute(query, params);

        assertEquals(1, res.columnAs("n").length());

        res.close();
    }
}