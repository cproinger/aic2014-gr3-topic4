package at.tuwien.aic2014.gr3.graphdb;

import at.tuwien.aic2014.gr3.domain.TwitterUser;
import at.tuwien.aic2014.gr3.shared.TwitterUserRepository;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.*;
import org.springframework.stereotype.Repository;

@Repository
public class Neo4jTwitterUserRepository implements TwitterUserRepository {

    private final static Logger log = Logger.getLogger(Neo4jTwitterUserRepository.class);
    public final static String TWITTER_USER_ID_PROP = "twitterUserId";

    private GraphDatabaseService graphDb;
    private TwitterUserRelationshipHandlerFactory twitterUserRelationshipHandlerFactory;

    public final static Label TWITTER_USER_NODE_LABEL = () -> "TwitterUser";

    public void setGraphDb(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;
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

        ResourceIterator<Node> it = graphDb
                .findNodesByLabelAndProperty(TWITTER_USER_NODE_LABEL, TWITTER_USER_ID_PROP, userId)
                .iterator();

        if (it.hasNext()) {
            twitterUser = new TwitterUser();
            Node userNode = it.next();
            //hack to cast Integer to Long, as it seems neo4j uses Integer for
            //id persistence.
            twitterUser.setId(Long.parseLong(String.valueOf(userNode.getProperty(TWITTER_USER_ID_PROP))));
        }

        log.debug("Read twitter user: " + (twitterUser != null ? twitterUser.getId() : null));

        return twitterUser;
    }

    public TwitterUserRelationshipHandler relation(TwitterUser twitterUser) {
        return this.twitterUserRelationshipHandlerFactory
                .createTwitterUserRelationshipHandler(twitterUser);
    }
}
