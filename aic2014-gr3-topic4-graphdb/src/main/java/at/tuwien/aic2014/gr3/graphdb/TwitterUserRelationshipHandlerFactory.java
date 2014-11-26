package at.tuwien.aic2014.gr3.graphdb;

import at.tuwien.aic2014.gr3.domain.TwitterUser;
import org.neo4j.rest.graphdb.RestGraphDatabase;

class TwitterUserRelationshipHandlerFactory {

    private RestGraphDatabase graphDb;

    public void setGraphDb(RestGraphDatabase graphDb) {
        this.graphDb = graphDb;
    }

    public TwitterUserRelationshipHandler
            createTwitterUserRelationshipHandler(TwitterUser twitterUser) {
        return new Neo4jTwitterUserRelationshipHandler(graphDb, twitterUser);
    }
}
