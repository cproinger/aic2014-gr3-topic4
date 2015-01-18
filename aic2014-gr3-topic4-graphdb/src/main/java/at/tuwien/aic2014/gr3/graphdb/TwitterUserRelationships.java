package at.tuwien.aic2014.gr3.graphdb;

import org.neo4j.graphdb.RelationshipType;

public enum TwitterUserRelationships implements RelationshipType {
    MENTIONED,
    RETWEETED,
    IS_FRIEND_OF,
    REPLIED_TO,
    MENTIONED_HASHTAG,
    MENTIONED_TOPIC
}
