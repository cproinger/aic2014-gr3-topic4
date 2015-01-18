# AIC-Aufgabe 2014 Topic 4 Gruppe 3

## Executable Classes

* at.tuwien.aic2014.gr3.twitter.StreamApiMiner.main(String[]): holt sich über 2 minuten tweets von der streaming-api
* at.tuwien.aic2014.gr3.twitter.TimelineApiMiner.main(String[]): benutzt die timeline-api um Tweet-Blöcke von Usern zu holen bis 100.000 tweets in der DB sind. benutzt den StreamApiMiner wenn die Tweet-Fenster welche aktuell in der DB vorhanden sind nicht ausreichen.  

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