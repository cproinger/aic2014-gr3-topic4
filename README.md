# AIC-Aufgabe 2014 Topic 4 Gruppe 3

## Installation requirements

* mongodb v2.6.5
* neo4j v2.1.6-1
* postgresql v9.3.5-1
* Oracle jdk v1.8.0_20
* Apache Maven v3.2.3

## Installation steps

1)

Start a mongodb server (listening on localhost and on default port) which will be used in the unit-tests (the DB aicdocstore-test will be used in the tests instead of aicdocstore)

2)

Configure twitter4j with the authentication credentials for using the Twitter API via one of the configuration options described here http://twitter4j.org/en/configuration.html. The Properties are not needed in the tests and do not need to be in the application-packages, but if you decide to package the application with the property-file you will need to place it under the following path: $(PROJECT_ROOT_DIRECTORY)/aic2014-gr3-topic4-twitter/src/main/resources/. The following properties need to be provided:  

```
debug=true
oauth.consumerKey=
oauth.consumerSecret=
oauth.accessToken=
oauth.accessTokenSecret=
    Consult the Twitter API documentation for more details on this fields.
```    
3)

In the root directory of the project run the following command:

```
mvn install -P tomcat-exec
```        

## Deployment and execution instructions:

During the installation step, following artifacts (in form of executable jar files, containing all required dependencies and resources) are created:
* $(PROJECT_ROOT_DIRECTORY)/aic2014-gr3-topic4-twitter/target/streamingapi_miner-jar-with-dependencies.jar -> collects tweets over the Twitter Streaming API. The data collected is persisted in the mongodb and will serve as basis for further data collection strategies.
* $(PROJECT_ROOT_DIRECTORY)/aic2014-gr3-topic4-twitter/target/timelineapi_miner-jar-with-dependencies.jar -> starting from the data collected with the above mentioned component, this entity collects further data, by addressing the timeline API of the Twitter, extracting more tweets for the already existent users. The new collected data is also persisted in the mongodb.
* $(PROJECT_ROOT_DIRECTORY)/aic2014-gr3-topic4-twitter/target/dataset_miner-jar-with-dependencies.jar -> the above two components collect data which may not yield strong connectivity. Therefore, it makes a step forward and collects further tweets, from closely related twitter users, from the circles of the existent user base, also persisting the data into mongodb.
* $(PROJECT_ROOT_DIRECTORY)/aic2014-gr3-topic4-graphdb/target/offline_tweets_miner-jar-with-dependencies.jar -> until now, all components just collected data, which was persisted in mongodb, without any processing. This entity iterates over the collection of tweets existent in mongodb and extracts relationships between users, topics and hashtags, which are persisted in neo4j. Metadata over the processed users is also persisted in postgresql.
* $(PROJECT_ROOT_DIRECTORY)/aic2014-gr3-topic4-twitter/target/relationship_miner-jar-with-dependencies.jar -> with the above mentioned components, there is no possibility to extract friend relationships between users. This entity, iterates over the users existent in postgres and retrieves over the Twitter API friends and followers for them, together with some tweets from those users, processing the data immediately and persisting the derived information in postgresql and in neo4j.
* $(PROJECT_ROOT_DIRECTORY)/aic2014-gr3-topic4-webapp/target/aic2014-gr3-topic4-webapp-0.0.1-SNAPSHOT-war-exec.jar. This is an executable jar that will start a tomcat7-server with the app. Use “java -jar <jar-name> -Dorg.neo4j.rest.read_timeout=300” to start. The application will be available at http://localhost:8080/

Before executing any of the above listed artifacts, assure that:
* mongod
* postgresql
* neo4j
are up and running with default configuration (listen on localhost, on their respective default ports; for postgresql, the postgres user, with the postgres password is used). You can override the default-properties for postgresql with system-properties: the defaults are

```
tweetsMiner.neo4j.url=http://127.0.0.1:7474/db/data
tweetsMiner.sql.url=jdbc:postgresql://127.0.0.1:5432/tweeter_users_db
tweetsMiner.sql.driver=org.postgresql.Driver
tweetsMiner.sql.username=postgres
tweetsMiner.sql.password=postgres
```

## Executable Classes

* at.tuwien.aic2014.gr3.twitter.StreamApiMiner.main(String[]): holt sich über 2 minuten tweets von der streaming-api
* at.tuwien.aic2014.gr3.twitter.DatasetMiner.main(String[]): benutzt die timeline-api um Tweet-Blöcke von Usern zu holen bis 100.000 tweets in der DB sind. benutzt den StreamApiMiner wenn die Tweet-Fenster welche aktuell in der DB vorhanden sind nicht ausreichen.  

## Neo4j setup

in neo4j.properties add  (if this is not done, adding the executiontime-property to the neo4j-server.properties will result in the error-message "Starting Neo4j Server failed: Unable to use guard, you have to enable guard in neo4j.properties").

```
# timeout  
execution_guard_enabled=true
```

then in neo4j-server.properties add  
```
# timeout 2 minutes
org.neo4j.server.webserver.limit.executiontime=120000
```