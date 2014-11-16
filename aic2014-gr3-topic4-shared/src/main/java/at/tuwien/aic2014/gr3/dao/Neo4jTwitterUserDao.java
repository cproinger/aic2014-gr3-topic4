package at.tuwien.aic2014.gr3.dao;

import at.tuwien.aic2014.gr3.domain.TwitterUser;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.*;

public class Neo4jTwitterUserDao implements TwitterUserDao {

    private final static Logger log = Logger.getLogger(Neo4jTwitterUserDao.class);
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
    public TwitterUser create(TwitterUser twitterUser) {
        log.debug("Creating twitter user node...");

        assert (twitterUser.getId() > 0);
        if (this.readById(twitterUser.getId()) != null) {
            log.debug ("Twitter User " + twitterUser.getId() + " already exists!");
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
    public TwitterUser readById(long id) {
        log.debug("Reading twitter user by ID...");
        TwitterUser twitterUser = null;

        ResourceIterator<Node> it = graphDb
                .findNodesByLabelAndProperty(TWITTER_USER_NODE_LABEL, TWITTER_USER_ID_PROP, id)
                .iterator();

        if (it.hasNext()) {
            twitterUser = new TwitterUser();
            Node userNode = it.next();
            twitterUser.setId((long)userNode.getProperty(TWITTER_USER_ID_PROP));
        }

        log.debug("Read twitter user: " + (twitterUser != null ? twitterUser.getId() : null));

        return twitterUser;
    }

    @Override
    public void update(TwitterUser twitterUser) {
        //Does nothing, as there is nothing to update here.
    }

    public TwitterUserRelationshipHandler user(TwitterUser twitterUser) {
        return this.twitterUserRelationshipHandlerFactory
                .createTwitterUserRelationshipHandler(twitterUser);
    }
}
