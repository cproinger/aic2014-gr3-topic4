package at.tuwien.aic2014.gr3.graphdb;

import at.tuwien.aic2014.gr3.domain.TwitterUser;
import at.tuwien.aic2014.gr3.shared.RepositoryException;
import at.tuwien.aic2014.gr3.shared.RepositoryIterator;
import at.tuwien.aic2014.gr3.shared.TwitterUserRepository;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Repository
public class Neo4jTwitterUserRepository implements TwitterUserRepository {

    private final static Logger log = Logger.getLogger(Neo4jTwitterUserRepository.class);
    public final static String TWITTER_USER_ID_PROP = "twitterUserId";

    private RestGraphDatabase graphDb;
    private RestCypherQueryEngine engine;
    private TwitterUserRelationshipHandlerFactory twitterUserRelationshipHandlerFactory;

    public final static Label TWITTER_USER_NODE_LABEL = () -> "TwitterUser";

    public void setGraphDb(RestGraphDatabase graphDb) {
        this.graphDb = graphDb;
        engine = new RestCypherQueryEngine(graphDb.getRestAPI());
    }

    public void setTwitterUserRelationshipHandlerFactory(TwitterUserRelationshipHandlerFactory twitterUserRelationshipHandlerFactory) {
        this.twitterUserRelationshipHandlerFactory = twitterUserRelationshipHandlerFactory;
    }

    @Override
    public TwitterUser save(TwitterUser twitterUser) {
        log.debug("Creating twitter user node...");

        assert (twitterUser.getId() > 0);
        if (this.readById(twitterUser.getId()) != null) {
            log.debug ("Twitter User " + twitterUser.getId() + " already exists! Nothing to be done");
            return twitterUser;
        }

        Transaction tx = graphDb.beginTx();

        Node userNode = graphDb.createNode(TWITTER_USER_NODE_LABEL);
        userNode.setProperty(TWITTER_USER_ID_PROP, twitterUser.getId());

        tx.success();

        log.debug("Twitter user node successfully created!");

        return twitterUser;
    }

    @Override
    public TwitterUser readById(long userId) {
        log.debug("Reading twitter user by ID...");
        TwitterUser twitterUser = null;

        Transaction tx = graphDb.beginTx();

        ResourceIterator<Node> it = graphDb
                .findNodesByLabelAndProperty(TWITTER_USER_NODE_LABEL, TWITTER_USER_ID_PROP, userId)
                .iterator();

        if (it.hasNext()) {
            twitterUser = twitterUserFromNode(it.next());
        }

        tx.success();

        log.debug("Read twitter user: " + (twitterUser != null ? twitterUser.getId() : null));

        return twitterUser;
    }

    @Override
    public RepositoryIterator<TwitterUser> readAll() throws RepositoryException {
        log.debug("Reading all twitter users from neo4j...");

        String query = String.format("MATCH (user:%s) RETURN user", TWITTER_USER_NODE_LABEL.name());
        QueryResult<Map<String, Object>> result = engine.query(query, null);

        log.debug("Found " + result + " TwitterUsers in neo4j");
        Iterator<Map<String, Object>> wrappedIt = result.iterator();

        return new RepositoryIterator<TwitterUser>() {

            @Override
            public boolean hasNext() {
                return wrappedIt.hasNext();
            }

            @Override
            public TwitterUser next() {
                return twitterUserFromNode((Node) wrappedIt.next().get("user"));
            }

            @Override
            public void finish() {
                /* Nothing to be released */
            }
        };
    }

    public TwitterUserRelationshipHandler relation(TwitterUser twitterUser) {
        return this.twitterUserRelationshipHandlerFactory
                .createTwitterUserRelationshipHandler(twitterUser);
    }

    private TwitterUser twitterUserFromNode(Node twitterUserNode) {
        TwitterUser twitterUser = new TwitterUser();

        //hack to cast Integer to Long, as it seems neo4j uses Integer for
        //id persistence.
        twitterUser.setId(Long.parseLong(String.valueOf(twitterUserNode.getProperty(TWITTER_USER_ID_PROP))));

        return twitterUser;
    }
}
