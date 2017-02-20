# cqrs-eventsourcing-kafka

# Structure

# Pre-Requisities
* JDK 1.8
* Maven 3.3.x
* (optional) Docker (for Kafka)

#  Building
`mvn clean install`

# Running Services
## Kafka
`docker run -p 2181:2181 -p 9092:9092 --env ADVERTISED_HOST=127.0.0.1 --env ADVERTISED_PORT=9092 spotify/kafka`

## API Micro-Service
```
cd services-inventoryitem-api/target
java -jar services-inventoryitem-api-1.0-SNAPSHOT.jar server application.yml
```
## Domain Micro-Service
```
cd services-inventoryitem-domain/target
java -jar services-inventoryitem-domain-1.0-SNAPSHOT.jar server application.yml
```

# TODO
* Get rid of ID.class 
* ~~Generate all the events and commands from JSON schema~~
* Add swagger documentation for API part
* Implement UI Micro-Service
