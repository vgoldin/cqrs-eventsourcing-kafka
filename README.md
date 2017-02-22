# CQRS and Event Sourcing Implementation with Kafka

# Introduction
The aim of this small project is to illustrate the possibility of applying CQRS, Domain-Driven Design and Event Sourcing in a simple way, enabling Reactive architecture and leveraging Kafka as a distributed message broker

See:
https://speakerdeck.com/vgoldin/es-and-kafka

# Structure
Module | Purpose
--- | --- | ---
`services-core` | core marker interfaces shared between all other modules
`services-infrastructure-eventstore` | event store implementation with JDBM 2.0
`services-infrastructure-messaging` | event publisher and command listener implementation with Kafka
`services-intentoryitem-schema` | json-schema's of all the events and commands used in API and Domain services
`services-inventoryitem-api` | API micro-service, exposing RESTfull API, creating Projectin with Hazelcast and dispatching commands to Kafka
`services-inventoryitem-domain` | Domain micro-service, encapsulating Aggregate Root, handling commands and publishing events

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
# Exposed APIs
```
GET     /inventory-items (io.plumery.inventoryitem.api.resources.InventoryItemResource)
POST    /inventory-items (io.plumery.inventoryitem.api.resources.InventoryItemResource)
GET     /inventory-items/events.stream (io.plumery.inventoryitem.api.resources.InventoryItemResource)
PUT     /inventory-items/{id} (io.plumery.inventoryitem.api.resources.InventoryItemResource)
POST    /inventory-items/{id}/deactivate (io.plumery.inventoryitem.api.resources.InventoryItemResource)
```    
# Replaying Projection
- Download and unzip Kafka
- Change to KAFKA_HOME
- Stop API Micro-Service
- Execute the following command:
```
./bin/kafka-streams-application-reset.sh --application-id InventoryItemsDenormalizationApplication \
                                      --input-topics InventoryItem \
                                      --bootstrap-servers localhost:9092 \
                                      --zookeeper localhost:2181
```

# TODO
* Get rid of ID.class 
* ~~Generate all the events and commands from JSON schema~~
* Add swagger documentation for API part
* Implement UI Micro-Service
