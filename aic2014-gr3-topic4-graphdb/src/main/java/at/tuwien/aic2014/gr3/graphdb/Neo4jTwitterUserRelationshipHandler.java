package at.tuwien.aic2014.gr3.graphdb;

import at.tuwien.aic2014.gr3.domain.TwitterUser;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.*;

public class Neo4jTwitterUserRelationshipHandler implements  TwitterUserRelationshipHandler {

    public static final String HASHTAG_NAME_PROP = "hashtag";
    public static final Label HASHTAG_LABEL = () -> "hashtag";
    public static final String TOPIC_NAME_PROP = "topic";
    public static final Label TOPIC_LABEL = () -> "topic";

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

        userNode.createRelationshipTo(followedUserNode, TwitterUserRelationships.FOLLOWS);
    }

    @Override
    public void mentioned(TwitterUser twitterUser) {
        log.debug("Twitter user " + this.twitterUser.getId() +
                " -> mentioned -> Twitter user " + twitterUser.getId());

        Node userNode = this.getOrCreateUserNode(this.twitterUser);
        Node mentionedUserNode = this.getOrCreateUserNode(twitterUser);

        userNode.createRelationshipTo(mentionedUserNode, TwitterUserRelationships.MENTIONED);
    }

    @Override
    public void retweeted(TwitterUser twitterUser) {
        log.debug("Twitter user " + this.twitterUser.getId() +
                " -> retweeted -> Twitter user " + twitterUser.getId());

        Node userNode = this.getOrCreateUserNode(this.twitterUser);
        Node retweetedUserNode = this.getOrCreateUserNode(twitterUser);

        userNode.createRelationshipTo(retweetedUserNode, TwitterUserRelationships.RETWEETED);
    }

    @Override
    public void isFriendOf(TwitterUser twitterUser) {
        log.debug("Twitter user " + this.twitterUser.getId() +
                " -> is friend of -> Twitter user " + twitterUser.getId());

        Node userNode = this.getOrCreateUserNode(this.twitterUser);
        Node isFriendOfUserNode = this.getOrCreateUserNode(twitterUser);

        userNode.createRelationshipTo(isFriendOfUserNode, TwitterUserRelationships.IS_FRIEND_OF);
    }

    @Override
    public void repliedTo(TwitterUser twitterUser) {
        log.debug("Twitter user " + this.twitterUser.getId() +
                " -> replied to -> Twitter user " + twitterUser.getId());

        Node userNode = this.getOrCreateUserNode(this.twitterUser);
        Node repliedToUserNode = this.getOrCreateUserNode(twitterUser);

        userNode.createRelationshipTo(repliedToUserNode, TwitterUserRelationships.REPLIED_TO);
    }

    @Override
    public void mentionedHashtag(String hashtag) {
        log.debug("Twitter user " + this.twitterUser.getId() +
                " -> mentioned -> Hashtag " + hashtag);

        Node userNode = this.getOrCreateUserNode(this.twitterUser);
        Node hashtagNode = this.getOrCreateHashtag(hashtag);

        userNode.createRelationshipTo(hashtagNode, TwitterUserRelationships.MENTIONED_HASHTAG);
    }

    @Override
    public void mentionedTopic(String topic) {
        log.debug("Twitter user " + this.twitterUser.getId() +
                " -> mentioned -> Topic " + topic);

        Node userNode = this.getOrCreateUserNode(this.twitterUser);
        Node topicNode= this.getOrCreateTopic(topic);

        userNode.createRelationshipTo(topicNode, TwitterUserRelationships.MENTIONED_TOPIC);
    }

    private Node getOrCreateUserNode(TwitterUser twitterUser) {
        ResourceIterator<Node> it = graphDb
                .findNodesByLabelAndProperty(Neo4jTwitterUserRepository.TWITTER_USER_NODE_LABEL,
                        Neo4jTwitterUserRepository.TWITTER_USER_ID_PROP, twitterUser.getId())
                .iterator();

        if (it.hasNext()) {
            log.debug("Found twitter user node for " + twitterUser.getId());
            return it.next();
        }

        Node userNode = graphDb.createNode(Neo4jTwitterUserRepository.TWITTER_USER_NODE_LABEL);
        userNode.setProperty(Neo4jTwitterUserRepository.TWITTER_USER_ID_PROP, twitterUser.getId());

        log.debug("Twitter user node " + twitterUser.getId() + " successfully created!");

        return userNode;
    }

    private Node getOrCreateHashtag(String hashtag) {
        ResourceIterator<Node> it = graphDb
                .findNodesByLabelAndProperty(HASHTAG_LABEL, HASHTAG_NAME_PROP, hashtag)
                .iterator();

        if (it.hasNext()) {
            log.debug("Found hashtag node for " + hashtag);
            return it.next();
        }

        Node hashtagNode = graphDb.createNode(HASHTAG_LABEL);
        hashtagNode.setProperty(HASHTAG_NAME_PROP, hashtag);

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

        Node hashtagNode = graphDb.createNode(TOPIC_LABEL);
        hashtagNode.setProperty(TOPIC_NAME_PROP, topic);

        log.debug("Hashtag node " + topic + " successfully created!");

        return hashtagNode;
    }
}
