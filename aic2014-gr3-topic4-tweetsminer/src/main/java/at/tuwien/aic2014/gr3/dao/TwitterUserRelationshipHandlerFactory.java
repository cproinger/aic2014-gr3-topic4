package at.tuwien.aic2014.gr3.dao;

import at.tuwien.aic2014.gr3.domain.TwitterUser;
import org.neo4j.graphdb.GraphDatabaseService;

class TwitterUserRelationshipHandlerFactory {

    private GraphDatabaseService graphDb;

    public void setGraphDb(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;
    }

    public TwitterUserRelationshipHandler
            createTwitterUserRelationshipHandler(TwitterUser twitterUser) {
        return new Neo4jTwitterUserRelationshipHandler(graphDb, twitterUser);
    }
}
