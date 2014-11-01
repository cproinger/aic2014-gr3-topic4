package at.tuwien.aic2014.gr3.dao;

import at.tuwien.aic2014.gr3.domain.TwitterUser;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.*;

enum TwitterUserRelationships implements RelationshipType {
    FOLLOWS,
    MENTIONED,
    RETWEETED,
    IS_FRIEND_OF,
    REPLIED_TO,
    MENTIONED_HASHTAG,
    MENTIONED_TOPIC
}

class Neo4jTwitterUserRelationshipHandler implements  TwitterUserRelationshipHandler {

    private final static Logger log = Logger.getLogger(Neo4jTwitterUserRelationshipHandler.class);

    private GraphDatabaseService graphDb;
    private TwitterUser twitterUser;

    public Neo4jTwitterUserRelationshipHandler(GraphDatabaseService graphDb, TwitterUser twitterUser) {
        this.graphDb = graphDb;
        this.twitterUser = twitterUser;
    }

    @Override
    public void follows(TwitterUser twitterUser) {
        log.debug("Twitter user " + this.twitterUser.getId() +
                " -> follows -> Twitter user " + twitterUser.getId());

        Node userNode = this.getOrCreateUserNode(this.twitterUser);
        Node followedUserNode = this.getOrCreateUserNode(twitterUser);

        Transaction tx = graphDb.beginTx();

        userNode.createRelationshipTo(followedUserNode, TwitterUserRelationships.FOLLOWS);

        tx.success();
    }

    @Override
    public void mentioned(TwitterUser twitterUser) {
        log.debug("Twitter user " + this.twitterUser.getId() +
                " -> mentioned -> Twitter user " + twitterUser.getId());

        Node userNode = this.getOrCreateUserNode(this.twitterUser);
        Node mentionedUserNode = this.getOrCreateUserNode(twitterUser);

        Transaction tx = graphDb.beginTx();

        userNode.createRelationshipTo(mentionedUserNode, TwitterUserRelationships.MENTIONED);

        tx.success();
    }

    @Override
    public void retweeted(TwitterUser twitterUser) {
        log.debug("Twitter user " + this.twitterUser.getId() +
                " -> retweeted -> Twitter user " + twitterUser.getId());

        Node userNode = this.getOrCreateUserNode(this.twitterUser);
        Node retweetedUserNode = this.getOrCreateUserNode(twitterUser);

        Transaction tx = graphDb.beginTx();

        userNode.createRelationshipTo(retweetedUserNode, TwitterUserRelationships.RETWEETED);

        tx.success();
    }

    @Override
    public void isFriendOf(TwitterUser twitterUser) {
        log.debug("Twitter user " + this.twitterUser.getId() +
                " -> is friend of -> Twitter user " + twitterUser.getId());

        Node userNode = this.getOrCreateUserNode(this.twitterUser);
        Node isFriendOfUserNode = this.getOrCreateUserNode(twitterUser);

        Transaction tx = graphDb.beginTx();

        userNode.createRelationshipTo(isFriendOfUserNode, TwitterUserRelationships.IS_FRIEND_OF);

        tx.success();
    }

    @Override
    public void repliedTo(TwitterUser twitterUser) {
        log.debug("Twitter user " + this.twitterUser.getId() +
                " -> replied to -> Twitter user " + twitterUser.getId());

        Node userNode = this.getOrCreateUserNode(this.twitterUser);
        Node repliedToUserNode = this.getOrCreateUserNode(twitterUser);

        Transaction tx = graphDb.beginTx();

        userNode.createRelationshipTo(repliedToUserNode, TwitterUserRelationships.REPLIED_TO);

        tx.success();
    }

    @Override
    public void mentionedHashtag(String hashtag) {
        log.debug("Twitter user " + this.twitterUser.getId() +
                " -> mentioned -> Hashtag " + hashtag);

        Node userNode = this.getOrCreateUserNode(this.twitterUser);
        Node hashtagNode = this.getOrCreateHashtag(hashtag);

        Transaction tx = graphDb.beginTx();

        userNode.createRelationshipTo(hashtagNode, TwitterUserRelationships.MENTIONED_HASHTAG);

        tx.success();
    }

    @Override
    public void mentionedTopic(String topic) {
        log.debug("Twitter user " + this.twitterUser.getId() +
                " -> mentioned -> Topic " + topic);

        Node userNode = this.getOrCreateUserNode(this.twitterUser);
        Node topicNode= this.getOrCreateTopic(topic);

        Transaction tx = graphDb.beginTx();

        userNode.createRelationshipTo(topicNode, TwitterUserRelationships.MENTIONED_TOPIC);

        tx.success();
    }

    private Node getOrCreateUserNode(TwitterUser twitterUser) {
        ResourceIterator<Node> it = graphDb
                .findNodesByLabelAndProperty(Neo4jTwitterUserDao.TWITTER_USER_NODE_LABEL,
                        Neo4jTwitterUserDao.TWITTER_USER_ID_PROP, twitterUser.getId())
                .iterator();

        if (it.hasNext()) {
            log.debug("Found twitter user node for " + twitterUser.getId());
            return it.next();
        }

        Transaction tx = graphDb.beginTx();

        Node userNode = graphDb.createNode(Neo4jTwitterUserDao.TWITTER_USER_NODE_LABEL);
        userNode.setProperty(Neo4jTwitterUserDao.TWITTER_USER_ID_PROP, twitterUser.getId());

        tx.success();

        log.debug("Twitter user node " + twitterUser.getId() + " successfully created!");

        return userNode;
    }

    protected static final String HASHTAG_NAME_PROP = "hashtag";
    protected static final Label HASHTAG_LABEL = () -> "hashtag";

    protected static final String TOPIC_NAME_PROP = "topic";
    protected static final Label TOPIC_LABEL = () -> "topic";

    private Node getOrCreateHashtag(String hashtag) {
        ResourceIterator<Node> it = graphDb
                .findNodesByLabelAndProperty(HASHTAG_LABEL, HASHTAG_NAME_PROP, hashtag)
                .iterator();

        if (it.hasNext()) {
            log.debug("Found hashtag node for " + hashtag);
            return it.next();
        }

        Transaction tx = graphDb.beginTx();

        Node hashtagNode = graphDb.createNode(HASHTAG_LABEL);
        hashtagNode.setProperty(HASHTAG_NAME_PROP, hashtag);

        tx.success();

        log.debug("Hashtag node " + hashtag + " successfully created!");

        return hashtagNode;
    }

    private Node getOrCreateTopic(String topic) {
        ResourceIterator<Node> it = graphDb
                .findNodesByLabelAndProperty(TOPIC_LABEL, TOPIC_NAME_PROP, topic)
                .iterator();

        if (it.hasNext()) {
            log.debug("Found hashtag node for " + topic);
            return it.next();
        }

        Transaction tx = graphDb.beginTx();

        Node hashtagNode = graphDb.createNode(TOPIC_LABEL);
        hashtagNode.setProperty(TOPIC_NAME_PROP, topic);

        tx.success();

        log.debug("Hashtag node " + topic + " successfully created!");

        return hashtagNode;
    }
}
