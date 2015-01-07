package at.tuwien.aic2014.gr3.graphdb;

import at.tuwien.aic2014.gr3.domain.InterestedUsers;
import at.tuwien.aic2014.gr3.domain.TwitterUser;
import at.tuwien.aic2014.gr3.domain.UserAndCount;
import at.tuwien.aic2014.gr3.shared.RepositoryIterator;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:tweetsMinerTestContext.xml")
public class Neo4jTwitterUserRepositoryTest {

    private static final String dropAllQuery =
            "MATCH (n) " +
            "OPTIONAL MATCH (n)-[r]-() " +
            "DELETE n,r";

    @Autowired
    private RestCypherQueryEngine engine;

    @Autowired
    private GraphDatabaseService graphdb;

    @Autowired
    private Neo4jTwitterUserRepository neo4jTwitterUserDao;

    @Autowired
    private TwitterUser testTwitterUser;

    private void assertHashtagUniquePresence(String hashtag) {
        String query =
                "MATCH (n:" + Neo4jTwitterUserRelationshipHandler.HASHTAG_LABEL.name() + ") " +
                "WHERE n." + Neo4jTwitterUserRelationshipHandler.HASHTAG_NAME_PROP + " = {hashtag} " +
                "RETURN n";
        Map<String, Object> params = new HashMap<>();
        params.put("hashtag", hashtag);

        Iterator it = engine.query(query, params).iterator();

        assertTrue(it.hasNext());
        it.next();
        assertFalse(it.hasNext());
    }

    private void assertTopicUniquePresence(String topic) {
        String query =
                "MATCH (n:" + Neo4jTwitterUserRelationshipHandler.TOPIC_LABEL.name() + ") " +
                "WHERE n." + Neo4jTwitterUserRelationshipHandler.TOPIC_NAME_PROP+ " = {topic} " +
                "RETURN n";
        Map<String, Object> params = new HashMap<>();
        params.put("topic", topic);

        Iterator it = engine.query(query, params).iterator();

        assertTrue(it.hasNext());
        it.next();
        assertFalse(it.hasNext());
    }

    private void assertUserRelationship (TwitterUser u1, String relationship, TwitterUser u2, int degree) {
        assertUserUniquePresence(u1);
        assertUserUniquePresence(u2);

        String query = String.format(
                "MATCH (u1:%s {%s:{u1_id}}) - [rel:%s] -> (u2:%s {%s:{u2_id}}) RETURN rel",
                Neo4jTwitterUserRepository.TWITTER_USER_NODE_LABEL.name(),
                Neo4jTwitterUserRepository.TWITTER_USER_ID_PROP,
                relationship,
                Neo4jTwitterUserRepository.TWITTER_USER_NODE_LABEL.name(),
                Neo4jTwitterUserRepository.TWITTER_USER_ID_PROP);
        Map<String, Object> params = new HashMap<>();
        params.put("u1_id", u1.getId());
        params.put("u2_id", u2.getId());

        Iterator it = engine.query(query, params).iterator();

        for (int i = 0; i < degree; ++i) {
            assertTrue(it.hasNext());
            it.next();
        }
        assertFalse(it.hasNext());
    }

    private void assertUserRelationshipHashtag (TwitterUser u1, String hashtag, int degree) {
        assertUserUniquePresence(u1);
        assertHashtagUniquePresence(hashtag);

        String query = String.format(
                "MATCH (u1:%s {%s:{u1_id}}) - [rel:%s] -> (hashtag:%s {%s:{hashtag}}) RETURN rel",
                Neo4jTwitterUserRepository.TWITTER_USER_NODE_LABEL.name(),
                Neo4jTwitterUserRepository.TWITTER_USER_ID_PROP,
                TwitterUserRelationships.MENTIONED_HASHTAG.name(),
                Neo4jTwitterUserRelationshipHandler.HASHTAG_LABEL.name(),
                Neo4jTwitterUserRelationshipHandler.HASHTAG_NAME_PROP);
        Map<String, Object> params = new HashMap<>();
        params.put("u1_id", u1.getId());
        params.put("hashtag", hashtag);

        Iterator it = engine.query(query, params).iterator();

        for (int i = 0; i < degree; ++i) {
            assertTrue(it.hasNext());
            it.next();
        }
        assertFalse(it.hasNext());
    }

    private void assertUserRelationshipTopic (TwitterUser u1, String topic, int degree) {
        assertUserUniquePresence(u1);
        assertTopicUniquePresence(topic);

        String query = String.format(
                "MATCH (u1:%s {%s:{u1_id}}) - [rel:%s] -> (topic:%s {%s:{topic}}) RETURN rel",
                Neo4jTwitterUserRepository.TWITTER_USER_NODE_LABEL.name(),
                Neo4jTwitterUserRepository.TWITTER_USER_ID_PROP,
                TwitterUserRelationships.MENTIONED_TOPIC.name(),
                Neo4jTwitterUserRelationshipHandler.TOPIC_LABEL.name(),
                Neo4jTwitterUserRelationshipHandler.TOPIC_NAME_PROP);
        Map<String, Object> params = new HashMap<>();
        params.put("u1_id", u1.getId());
        params.put("topic", topic);

        Iterator it = engine.query(query, params).iterator();

        for (int i = 0; i < degree; ++i) {
            assertTrue(it.hasNext());
            it.next();
        }
        assertFalse(it.hasNext());
    }

    private void assertUserUniquePresence(TwitterUser user) {
        String query =
                "MATCH (n:" + Neo4jTwitterUserRepository.TWITTER_USER_NODE_LABEL.name() + ") " +
                "WHERE n." + Neo4jTwitterUserRepository.TWITTER_USER_ID_PROP + " = {id} " +
                "RETURN n";
        Map<String, Object> params = new HashMap<>();
        params.put("id", user.getId());

        Iterator it = engine.query(query, params).iterator();

        assertTrue(it.hasNext());
        it.next();
        assertFalse(it.hasNext());
    }

    private TwitterUser createRetweetedUser(int degree) {
		TwitterUser newUser = new TwitterUser();
        newUser.setId(testTwitterUser.getId() + 1);

        for (int i = 0; i < degree; ++i) {
            neo4jTwitterUserDao.relation(testTwitterUser).retweeted(newUser);
        }
		return newUser;
	}

    @Before
    public void setUp() throws Exception {
        Transaction tx = graphdb.beginTx();

        Node testUserNode = graphdb.createNode(Neo4jTwitterUserRepository.TWITTER_USER_NODE_LABEL);
        testUserNode.setProperty(Neo4jTwitterUserRepository.TWITTER_USER_ID_PROP, testTwitterUser.getId());

        tx.success();
    }

    private void setUpHugeDataSet(int size) throws Exception {
        tearDown();

        Transaction tx = graphdb.beginTx();

        for (int i = 0; i < size; ++i) {
            Node testUserNode = graphdb.createNode(Neo4jTwitterUserRepository.TWITTER_USER_NODE_LABEL);
            testUserNode.setProperty(Neo4jTwitterUserRepository.TWITTER_USER_ID_PROP, i);
        }

        tx.success();
    }

    @After
    public void tearDown() throws Exception {
        Transaction tx = graphdb.beginTx();

        engine.query(dropAllQuery, null);

        tx.success();
    }

    @Test
    public void testCreate() throws Exception {
        TwitterUser user = new TwitterUser();
        user.setId(2);

        neo4jTwitterUserDao.save(user);

        assertUserUniquePresence(user);
    }

    @Test
    public void testCreateDuplicate() throws Exception {
        TwitterUser user = new TwitterUser();
        user.setId(2);

        neo4jTwitterUserDao.save(user);
        neo4jTwitterUserDao.save(user);

        assertUserUniquePresence(user);
    }

    @Test
    public void testFindMostRetweeted() {
    	TwitterUser retweetedUser = createRetweetedUser(15);
    	Iterable<UserAndCount> result = neo4jTwitterUserDao.findMostRetweetedUsers();
    	Iterator<UserAndCount> it = result.iterator();
    	assertEquals(true, it.hasNext());
    	assertEquals(retweetedUser.getId(), it.next().getUser().getId());
    	assertEquals(false, it.hasNext());
    }
    
    @Test
    public void testfindInterstedUsers() {
    	/*
    	 * setup userId, mentioned-topics
    	 * 0 - 10
    	 * 1 - 1
    	 * 2 - 30
    	 * 3 - 3
    	 * 4 - 50
    	 * 5 - 5
    	 */
    	for(int i = 0; i < 6; i++) {
    		TwitterUser tu = new TwitterUser();
    		tu.setId(this.testTwitterUser.getId() + 1 + i);
    		int topics = (i % 2 == 0 ? (i+1) * 10 : i);
    		for(int j = 0; j < topics; j++) {
    			String topic = "topic" + j;
				neo4jTwitterUserDao.relation(tu).mentionedTopic(topic);
    		}
    	}
    	InterestedUsers result = neo4jTwitterUserDao.findInterestedUsers();
    	
    	Iterator<UserAndCount> broadRange = result.getBroadRange().iterator();
    	assertUserAndCount(broadRange.next(), testTwitterUser.getId() + 1 + 4, 50);
		assertUserAndCount(broadRange.next(), testTwitterUser.getId() + 1 + 2, 30);
		assertUserAndCount(broadRange.next(), testTwitterUser.getId() + 1 + 0, 10);
		assertFalse(broadRange.hasNext());
		
		Iterator<UserAndCount> focused = result.getFocused().iterator();
    	assertUserAndCount(focused.next(), testTwitterUser.getId() + 1 + 1, 1);
		assertUserAndCount(focused.next(), testTwitterUser.getId() + 1 + 3, 3);
		assertUserAndCount(focused.next(), testTwitterUser.getId() + 1 + 5, 5);
		assertFalse(focused.hasNext());
    }

	private void assertUserAndCount(UserAndCount next, long userId, int count) {
		assertEquals("expected differentUser and count",
				Pair.of(userId, count),
				Pair.of(next.getUser().getId(), next.getCount()));
	}

    @Test
    public void testFollowsRelationshipMultiple() throws Exception {
        TwitterUser newUser = new TwitterUser();
        newUser.setId(testTwitterUser.getId() + 1);

        int degree = 10;
        for (int i = 0; i < degree; ++i) {
            neo4jTwitterUserDao.relation(testTwitterUser).follows(newUser);
        }

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.FOLLOWS.name(), newUser, degree);
    }

	@Test
    public void testFollowsRelationshipSingleWithExistentUser() throws Exception {
        neo4jTwitterUserDao.relation(testTwitterUser).follows(testTwitterUser);

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.FOLLOWS.name(), testTwitterUser, 1);
    }
    
    
    @Test
    public void testFollowsRelationshipSingleWithNewUser() throws Exception {
        TwitterUser newUser = new TwitterUser();
        newUser.setId(testTwitterUser.getId() + 1);

        neo4jTwitterUserDao.relation(testTwitterUser).follows(newUser);

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.FOLLOWS.name(), newUser, 1);
    }

    @Test
    public void testIsFriendOfRelationshipMultiple() throws Exception {
        TwitterUser newUser = new TwitterUser();
        newUser.setId(testTwitterUser.getId() + 1);

        int degree = 10;
        for (int i = 0; i < degree; ++i) {
            neo4jTwitterUserDao.relation(testTwitterUser).isFriendOf(newUser);
        }

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.IS_FRIEND_OF.name(), newUser, degree);
    }

    @Test
    public void testIsFriendOfRelationshipSingleWithExistentUser() throws Exception {
        neo4jTwitterUserDao.relation(testTwitterUser).isFriendOf(testTwitterUser);

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.IS_FRIEND_OF.name(), testTwitterUser, 1);
    }

    @Test
    public void testIsFriendOfRelationshipSingleWithNewUser() throws Exception {
        TwitterUser newUser = new TwitterUser();
        newUser.setId(testTwitterUser.getId() + 1);

        neo4jTwitterUserDao.relation(testTwitterUser).isFriendOf(newUser);

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.IS_FRIEND_OF.name(), newUser, 1);
    }

    @Test
    public void testMentionedHashtagRelationshipMultiple() throws Exception {
        String hashtag = "unit-testing";

        int degree = 10;
        for (int i = 0; i < degree; ++i) {
            neo4jTwitterUserDao.relation(testTwitterUser).mentionedHashtag(hashtag);
        }

        assertUserRelationshipHashtag(testTwitterUser, hashtag, degree);
    }

    @Test
    public void testMentionedHashtagRelationshipSingleWithExistentHashtag() throws Exception {
        String hashtag = "unit-testing";

        Transaction tx = graphdb.beginTx();
        Node hashtagNode = graphdb.createNode(Neo4jTwitterUserRelationshipHandler.HASHTAG_LABEL);
        hashtagNode.setProperty(Neo4jTwitterUserRelationshipHandler.HASHTAG_NAME_PROP, hashtag);
        tx.success();

        neo4jTwitterUserDao.relation(testTwitterUser).mentionedHashtag(hashtag);

        assertUserRelationshipHashtag(testTwitterUser, hashtag, 1);
    }

    @Test
    public void testMentionedHashtagRelationshipSingleWithNewHashtag() throws Exception {
        String hashtag = "unit-testing";

        neo4jTwitterUserDao.relation(testTwitterUser).mentionedHashtag(hashtag);

        assertUserRelationshipHashtag(testTwitterUser, hashtag, 1);
    }

    @Test
    public void testMentionedHashtagRelationshipSingleWithNewTopic() throws Exception {
        String topic = "unit-testing";

        neo4jTwitterUserDao.relation(testTwitterUser).mentionedTopic(topic);

        assertUserRelationshipTopic(testTwitterUser, topic, 1);
    }

    @Test
    public void testMentionedRelationshipMultiple() throws Exception {
        TwitterUser newUser = new TwitterUser();
        newUser.setId(testTwitterUser.getId() + 1);

        int degree = 10;
        for (int i = 0; i < degree; ++i) {
            neo4jTwitterUserDao.relation(testTwitterUser).mentioned(newUser);
        }

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.MENTIONED.name(), newUser, degree);
    }

    @Test
    public void testMentionedRelationshipSingleWithExistentUser() throws Exception {
        neo4jTwitterUserDao.relation(testTwitterUser).mentioned(testTwitterUser);

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.MENTIONED.name(), testTwitterUser, 1);
    }

    @Test
    public void testMentionedRelationshipSingleWithNewUser() throws Exception {
        TwitterUser newUser = new TwitterUser();
        newUser.setId(testTwitterUser.getId() + 1);

        neo4jTwitterUserDao.relation(testTwitterUser).mentioned(newUser);

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.MENTIONED.name(), newUser, 1);
    }

    @Test
    public void testMentionedTopicRelationshipMultiple() throws Exception {
        String topic = "unit-testing";

        int degree = 10;
        for (int i = 0; i < degree; ++i) {
            neo4jTwitterUserDao.relation(testTwitterUser).mentionedTopic(topic);
        }

        assertUserRelationshipTopic(testTwitterUser, topic, degree);
    }

    @Test
    public void testMentionedTopicRelationshipSingleWithExistentTopic() throws Exception {
        String topic = "unit-testing";

        Transaction tx = graphdb.beginTx();
        Node hashtagNode = graphdb.createNode(Neo4jTwitterUserRelationshipHandler.TOPIC_LABEL);
        hashtagNode.setProperty(Neo4jTwitterUserRelationshipHandler.TOPIC_NAME_PROP, topic);
        tx.success();

        neo4jTwitterUserDao.relation(testTwitterUser).mentionedTopic(topic);

        assertUserRelationshipTopic(testTwitterUser, topic, 1);
    }

    @Test
    public void testReadAll() throws Exception {
        RepositoryIterator<TwitterUser> it = neo4jTwitterUserDao.readAll();

        assertTrue(it.hasNext());
        assertEquals(testTwitterUser, it.next());
        assertFalse(it.hasNext());
    }

    @Test
    public void testReadAllHugeDataSet() throws Exception {
        int size = 30000;  //tested with 1000000.
        setUpHugeDataSet(size);

        RepositoryIterator<TwitterUser> it = neo4jTwitterUserDao.readAll();

        int nTotalTwitterUsers = 0;
        while (it.hasNext()) {
            it.next();
            nTotalTwitterUsers++;
        }

        assertEquals(size, nTotalTwitterUsers);
    }

    @Test
    public void testReadById() throws Exception {
        assertEquals(testTwitterUser, neo4jTwitterUserDao.readById(testTwitterUser.getId()));
    }

    @Test
    public void testRepliedToRelationshipMultiple() throws Exception {
        TwitterUser newUser = new TwitterUser();
        newUser.setId(testTwitterUser.getId() + 1);

        int degree = 10;
        for (int i = 0; i < degree; ++i) {
            neo4jTwitterUserDao.relation(testTwitterUser).repliedTo(newUser);
        }

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.REPLIED_TO.name(), newUser, degree);
    }

    @Test
    public void testRepliedToRelationshipSingleWithExistentUser() throws Exception {
        neo4jTwitterUserDao.relation(testTwitterUser).repliedTo(testTwitterUser);

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.REPLIED_TO.name(), testTwitterUser, 1);
    }

    @Test
    public void testRepliedToRelationshipSingleWithNewUser() throws Exception {
        TwitterUser newUser = new TwitterUser();
        newUser.setId(testTwitterUser.getId() + 1);

        neo4jTwitterUserDao.relation(testTwitterUser).repliedTo(newUser);

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.REPLIED_TO.name(), newUser, 1);
    }

    @Test
    public void testRetweetedRelationshipMultiple() throws Exception {
    	int degree = 10;
        TwitterUser newUser = createRetweetedUser(degree);
        assertUserRelationship(testTwitterUser, TwitterUserRelationships.RETWEETED.name(), newUser, degree);
    }

    @Test
    public void testRetweetedRelationshipSingleWithExistentUser() throws Exception {
        neo4jTwitterUserDao.relation(testTwitterUser).retweeted(testTwitterUser);

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.RETWEETED.name(), testTwitterUser, 1);
    }

    @Test
    public void testRetweetedRelationshipSingleWithNewUser() throws Exception {
        TwitterUser newUser = new TwitterUser();
        newUser.setId(testTwitterUser.getId() + 1);

        neo4jTwitterUserDao.relation(testTwitterUser).retweeted(newUser);

        assertUserRelationship(testTwitterUser, TwitterUserRelationships.RETWEETED.name(), newUser, 1);
    }
}