package at.tuwien.aic2014.gr3.dao;

import org.neo4j.graphdb.RelationshipType;

public enum TwitterUserRelationships implements RelationshipType {
    FOLLOWS,
    MENTIONED,
    RETWEETED,
    IS_FRIEND_OF,
    REPLIED_TO,
    MENTIONED_HASHTAG,
    MENTIONED_TOPIC
}
