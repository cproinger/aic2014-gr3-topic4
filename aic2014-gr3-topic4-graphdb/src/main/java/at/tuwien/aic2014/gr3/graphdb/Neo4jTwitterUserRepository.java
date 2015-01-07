package at.tuwien.aic2014.gr3.graphdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.ConvertedResult;
import org.neo4j.rest.graphdb.util.QueryResult;
import org.neo4j.rest.graphdb.util.ResultConverter;
import org.springframework.stereotype.Repository;

import at.tuwien.aic2014.gr3.domain.InterestedUsers;
import at.tuwien.aic2014.gr3.domain.TwitterUser;
import at.tuwien.aic2014.gr3.domain.UserAndCount;
import at.tuwien.aic2014.gr3.shared.AnalysisRepository;
import at.tuwien.aic2014.gr3.shared.RepositoryException;
import at.tuwien.aic2014.gr3.shared.RepositoryIterator;
import at.tuwien.aic2014.gr3.shared.TwitterUserRepository;

@Repository
public class Neo4jTwitterUserRepository implements TwitterUserRepository
	/* mal provisorisch hier gleich implementiert, kann sein das
	 * weitere queries es nötig machen dafür ein eigenes Repository
	 * vor dieses vorzuschalten. 
	 */
	, AnalysisRepository {

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

        //TODO use injected component to fetch sql data as well. 
        
        //hack to cast Integer to Long, as it seems neo4j uses Integer for
        //id persistence.
        twitterUser.setId(Long.parseLong(String.valueOf(twitterUserNode.getProperty(TWITTER_USER_ID_PROP))));

        return twitterUser;
    }
    
    @Override
    public Iterable<UserAndCount> findMostRetweetedUsers() {
    	String statement = "MATCH (a)-[:`RETWEETED`]->(b) "
    			+ "WITH b as usr, count(*) as c "
    			+ "order by c DESC "
    			+ "RETURN usr,c LIMIT 5";
		Map<String, Object> params = new HashMap<String, Object>();
		QueryResult<Map<String, Object>> result = engine.query(statement, params);
		ResultConverter<Map<String, Object>, UserAndCount> converter = new ResultConverter<Map<String, Object>, UserAndCount>() {
			@Override
			public UserAndCount convert(Map<String, Object> value,
					Class<UserAndCount> type) {
				TwitterUser usr = twitterUserFromNode((Node) value.get("usr"));
				Integer c = (Integer) value.get("c");
				return new UserAndCount(usr, c);
			}
		};
		ConvertedResult<UserAndCount> convertedResult = result.to(UserAndCount.class, converter);
		return convertedResult;
    }
   
    @Override
    public InterestedUsers findInterestedUsers() {
    	/*
    	 * vorkommenshäufigkeit = f(term, doc) / max(f(anyTerm, doc))
    	 * inverse dok-häufigkeit = log N / ni, ni ... Anzahl der docs die term i beinhalten
    	 * 
    	 * w(i,j) = tf(i,j) * idf(i);
    	 */
    	//leider gibt unsere neo4j-db die information für "term frequency-inverse document frequency"
    	//momentan nicht her, also vorerst mal nur topics zählen :-(. 
    	//was fehlt ist die anzahl der tweets = dokumente
    	//was man machen könnte ist den grad eines terms
    	//im verhältnis zu den graden anderer terms zu setzen
    	//dann ist ein user mit niedrigen verhältnissen "interseted in a broad range of topics"
    	//und user mit hohen verhältnissen "more focused". 
    	//=> damit das mal im ui angezeigt werden kann ist die jetzige implementierung
    	//mal gut genug. schließlich steht im text ja "may be to limiting". 
    	
    	//bei mir packt der neo4j-server keine 2 solche anfragen auf einmal
    	//glaub kaum das die VM da stärker sein wird
    	//deshalb nur ein single-thread-executor. 
    	ExecutorService exec = Executors.newSingleThreadExecutor();//Executors.newFixedThreadPool(2);
    	
		
		Future<QueryResult<Map<String, Object>>> broadRange = exec.submit(new Callable<QueryResult<Map<String, Object>>>() {

			@Override
			public QueryResult<Map<String, Object>> call() throws Exception {
				String statement = "MATCH m = (a)-[:MENTIONED_TOPIC]->(b) "
						+ "WITH a as usr, b as topic "
						+ "WITH usr, count(distinct topic) as cnt "
						+ "ORDER BY cnt DESC "
						+ "RETURN usr, cnt "
						+ "LIMIT 3";
				return engine.query(statement, null);
			}
		});
		
		Future<QueryResult<Map<String, Object>>> focused = exec.submit(new Callable<QueryResult<Map<String, Object>>>() {

			@Override
			public QueryResult<Map<String, Object>> call() throws Exception {
				String statement = "MATCH m = (a)-[:MENTIONED_TOPIC]->(b) "
						+ "WITH a as usr, b as topic "
						+ "WITH usr, count(distinct topic) as cnt "
						+ "ORDER BY cnt ASC "
						+ "RETURN usr, cnt "
						+ "LIMIT 3";
				return engine.query(statement, null);
			}
		});
		try {
			ArrayList<UserAndCount> broadRangeUsers = new ArrayList<UserAndCount>();
			for(Map<String, Object> m : broadRange.get()) {
				broadRangeUsers.add(new UserAndCount(twitterUserFromNode((Node) m.get("usr")), (int) m.get("cnt")));
			}
			ArrayList<UserAndCount> focusedUsers = new ArrayList<UserAndCount>();
			for(Map<String, Object> m : focused.get()) {
				focusedUsers.add(new UserAndCount(twitterUserFromNode((Node) m.get("usr")), (int) m.get("cnt")));
			}
			
			return new InterestedUsers(broadRangeUsers, focusedUsers);
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException("error in query", e);
		}
    }
}