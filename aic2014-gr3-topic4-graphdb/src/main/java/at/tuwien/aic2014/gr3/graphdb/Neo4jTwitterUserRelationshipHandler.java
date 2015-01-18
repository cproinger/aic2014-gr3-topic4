package at.tuwien.aic2014.gr3.graphdb;

import at.tuwien.aic2014.gr3.domain.TwitterUser;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.*;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;

public class Neo4jTwitterUserRelationshipHandler implements  TwitterUserRelationshipHandler {

    public static final String HASHTAG_NAME_PROP = "hashtag";
    public static final Label HASHTAG_LABEL = () -> "hashtag";
    public static final String TOPIC_NAME_PROP = "topic";
    public static final Label TOPIC_LABEL = () -> "topic";

    public static final String MENTIONED_COUNTER_PROP = "times";
    public static final String RETWEETED_COUNTER_PROP = "times";
    public static final String REPLIED_TO_COUNTER_PROP = "times";
    public static final String MENTIONED_HASHTAG_COUNTER_PROP = "times";
    public static final String MENTIONED_TOPIC_COUNTER_PROP = "times";

    private final static Logger log = Logger.getLogger(Neo4jTwitterUserRelationshipHandler.class);

    private RestGraphDatabase graphDb;
    private RestCypherQueryEngine engine;
    private TwitterUser twitterUser;

    public Neo4jTwitterUserRelationshipHandler(RestGraphDatabase graphDb, TwitterUser twitterUser) {
        this.graphDb = graphDb;
        this.twitterUser = twitterUser;
        engine = new RestCypherQueryEngine(graphDb.getRestAPI());
    }

    @Override
    public void follows(TwitterUser twitterUser) {
        log.debug("Twitter user " + this.twitterUser.getId() +
                " -> follows -> Twitter user " + twitterUser.getId());

        this.createUniqueUserNode(this.twitterUser);
        this.createUniqueUserNode(twitterUser);

        String query = String.format(
                "MATCH (u1:%s {%s:%d}), (u2:%s {%s:%d}) " +
                        "MERGE (u1)-[r:%s]->(u2) " +
                        "RETURN r",
                Neo4jTwitterUserRepository.TWITTER_USER_NODE_LABEL.name(),
                Neo4jTwitterUserRepository.TWITTER_USER_ID_PROP,
                this.twitterUser.getId(),
                Neo4jTwitterUserRepository.TWITTER_USER_NODE_LABEL.name(),
                Neo4jTwitterUserRepository.TWITTER_USER_ID_PROP,
                twitterUser.getId(),
                TwitterUserRelationships.FOLLOWS.name()
        );

        this.engine.query(query, null);
    }

    @Override
    public void mentioned(TwitterUser twitterUser) {
        log.debug("Twitter user " + this.twitterUser.getId() +
                " -> mentioned -> Twitter user " + twitterUser.getId());

        this.createUniqueUserNode(this.twitterUser);
        this.createUniqueUserNode(twitterUser);

        String query = String.format(
                "MATCH (u1:%s {%s:%d}), (u2:%s {%s:%d}) " +
                        "MERGE (u1)-[r:%s]->(u2) " +
                        "ON CREATE SET r.%s=1 " +
                        "ON MATCH SET r.%s = r.%s + 1 " +
                        "RETURN r",
                Neo4jTwitterUserRepository.TWITTER_USER_NODE_LABEL.name(),
                Neo4jTwitterUserRepository.TWITTER_USER_ID_PROP,
                this.twitterUser.getId(),
                Neo4jTwitterUserRepository.TWITTER_USER_NODE_LABEL.name(),
                Neo4jTwitterUserRepository.TWITTER_USER_ID_PROP,
                twitterUser.getId(),
                TwitterUserRelationships.MENTIONED.name(),
                MENTIONED_COUNTER_PROP, MENTIONED_COUNTER_PROP, MENTIONED_COUNTER_PROP
        );

        this.engine.query(query, null);
    }

    @Override
    public void retweeted(TwitterUser twitterUser) {
        log.debug("Twitter user " + this.twitterUser.getId() +
                " -> retweeted -> Twitter user " + twitterUser.getId());

        this.createUniqueUserNode(this.twitterUser);
        this.createUniqueUserNode(twitterUser);

        String query = String.format(
                "MATCH (u1:%s {%s:%d}), (u2:%s {%s:%d}) " +
                        "MERGE (u1)-[r:%s]->(u2) " +
                        "ON CREATE SET r.%s=1 " +
                        "ON MATCH SET r.%s = r.%s + 1 " +
                        "RETURN r",
                Neo4jTwitterUserRepository.TWITTER_USER_NODE_LABEL.name(),
                Neo4jTwitterUserRepository.TWITTER_USER_ID_PROP,
                this.twitterUser.getId(),
                Neo4jTwitterUserRepository.TWITTER_USER_NODE_LABEL.name(),
                Neo4jTwitterUserRepository.TWITTER_USER_ID_PROP,
                twitterUser.getId(),
                TwitterUserRelationships.RETWEETED.name(),
                RETWEETED_COUNTER_PROP, RETWEETED_COUNTER_PROP, RETWEETED_COUNTER_PROP
        );

        this.engine.query(query, null);
    }

    @Override
    public void isFriendOf(TwitterUser twitterUser) {
        log.debug("Twitter user " + this.twitterUser.getId() +
                " -> is friend of -> Twitter user " + twitterUser.getId());

        this.createUniqueUserNode(this.twitterUser);
        this.createUniqueUserNode(twitterUser);

        String query = String.format(
                "MATCH (u1:%s {%s:%d}), (u2:%s {%s:%d}) " +
                        "MERGE (u1)-[r:%s]-(u2) " +
                        "RETURN r",
                Neo4jTwitterUserRepository.TWITTER_USER_NODE_LABEL.name(),
                Neo4jTwitterUserRepository.TWITTER_USER_ID_PROP,
                this.twitterUser.getId(),
                Neo4jTwitterUserRepository.TWITTER_USER_NODE_LABEL.name(),
                Neo4jTwitterUserRepository.TWITTER_USER_ID_PROP,
                twitterUser.getId(),
                TwitterUserRelationships.IS_FRIEND_OF.name()
        );

        this.engine.query(query, null);
    }

    @Override
    public void repliedTo(TwitterUser twitterUser) {
        log.debug("Twitter user " + this.twitterUser.getId() +
                " -> replied to -> Twitter user " + twitterUser.getId());

        this.createUniqueUserNode(this.twitterUser);
        this.createUniqueUserNode(twitterUser);

        String query = String.format(
                "MATCH (u1:%s {%s:%d}), (u2:%s {%s:%d}) " +
                        "MERGE (u1)-[r:%s]->(u2) " +
                        "ON CREATE SET r.%s=1 " +
                        "ON MATCH SET r.%s = r.%s + 1 " +
                        "RETURN r",
                Neo4jTwitterUserRepository.TWITTER_USER_NODE_LABEL.name(),
                Neo4jTwitterUserRepository.TWITTER_USER_ID_PROP,
                this.twitterUser.getId(),
                Neo4jTwitterUserRepository.TWITTER_USER_NODE_LABEL.name(),
                Neo4jTwitterUserRepository.TWITTER_USER_ID_PROP,
                twitterUser.getId(),
                TwitterUserRelationships.REPLIED_TO.name(),
                REPLIED_TO_COUNTER_PROP, REPLIED_TO_COUNTER_PROP, REPLIED_TO_COUNTER_PROP
        );

        this.engine.query(query, null);
    }

    @Override
    public void mentionedHashtag(String hashtag) {
        log.debug("Twitter user " + this.twitterUser.getId() +
                " -> mentioned -> Hashtag " + hashtag);

        this.createUniqueUserNode(this.twitterUser);
        this.createUniqueHashtag(hashtag);

        String query = String.format(
                "MATCH (u:%s {%s:%d}), (h:%s {%s:'%s'}) " +
                        "MERGE (u)-[r:%s]->(h) " +
                        "ON CREATE SET r.%s=1 " +
                        "ON MATCH SET r.%s = r.%s + 1 " +
                        "RETURN r",
                Neo4jTwitterUserRepository.TWITTER_USER_NODE_LABEL.name(),
                Neo4jTwitterUserRepository.TWITTER_USER_ID_PROP,
                this.twitterUser.getId(),
                HASHTAG_LABEL.name(), HASHTAG_NAME_PROP, hashtag,
                TwitterUserRelationships.MENTIONED_HASHTAG.name(),
                MENTIONED_HASHTAG_COUNTER_PROP, MENTIONED_HASHTAG_COUNTER_PROP, MENTIONED_HASHTAG_COUNTER_PROP
        );

        this.engine.query(query, null);
    }

    @Override
    public void mentionedTopic(String topic) {
        log.debug("Twitter user " + this.twitterUser.getId() +
                " -> mentioned -> Topic " + topic);

        this.createUniqueUserNode(this.twitterUser);
        this.createUniqueTopic(topic);

        String query = String.format(
                "MATCH (u:%s {%s:%d}), (t:%s {%s:'%s'}) " +
                        "MERGE (u)-[r:%s]->(t) " +
                        "ON CREATE SET r.%s=1 " +
                        "ON MATCH SET r.%s = r.%s + 1 " +
                        "RETURN r",
                Neo4jTwitterUserRepository.TWITTER_USER_NODE_LABEL.name(),
                Neo4jTwitterUserRepository.TWITTER_USER_ID_PROP,
                this.twitterUser.getId(),
                TOPIC_LABEL.name(), TOPIC_NAME_PROP, topic,
                TwitterUserRelationships.MENTIONED_TOPIC.name(),
                MENTIONED_TOPIC_COUNTER_PROP, MENTIONED_TOPIC_COUNTER_PROP, MENTIONED_TOPIC_COUNTER_PROP
        );


        this.engine.query(query, null);
    }

    private void createUniqueUserNode(TwitterUser twitterUser) {
        assert (twitterUser.getId() > 0);

        String query = String.format(
                        "MERGE (u:%s {%s:%d}) " +
                        "ON CREATE SET u.%s = 0 " +
                        "RETURN u",
                Neo4jTwitterUserRepository.TWITTER_USER_NODE_LABEL.name(),
                Neo4jTwitterUserRepository.TWITTER_USER_ID_PROP,
                twitterUser.getId(),
                Neo4jTwitterUserRepository.TWITTER_USER_PROCESSED_STATUSES_COUNT_PROP);

        engine.query(query, null);

        log.debug("Twitter user node " + twitterUser.getId() + " successfully created!");
    }

    private void createUniqueHashtag (String hashtag) {
        String query = String.format(
                "MERGE (h:%s {%s:'%s'}) RETURN h",
                HASHTAG_LABEL.name(), HASHTAG_NAME_PROP, hashtag
        );

        engine.query(query, null);
    }

    private void createUniqueTopic (String topic) {
        String query = String.format(
                "MERGE (t:%s {%s:'%s'}) RETURN t",
                TOPIC_LABEL.name(), TOPIC_NAME_PROP, topic
        );

        engine.query(query, null);
    }
}
