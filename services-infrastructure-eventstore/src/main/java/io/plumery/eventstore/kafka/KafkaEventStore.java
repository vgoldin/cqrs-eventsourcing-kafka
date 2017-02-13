package io.plumery.eventstore.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.ClassPath;
import io.plumery.core.*;
import io.plumery.core.infrastructure.EventStore;
import io.plumery.eventstore.exception.AggregateNotFoundException;
import io.plumery.eventstore.exception.EventNotSupportedExeption;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkNotNull;

public class KafkaEventStore implements EventStore {
    private static final String EVENTSTORE_CONSUMER_SUFFIX = ".EventStore";
    private final String eventsPackage;
    private final ObjectMapper objectMapper;
    private final String groupId;
    private final String zookeeper;

    private KafkaEventStore(String zookeeper, String groupId, ObjectMapper objectMapper, String eventsPackage) {
        this.objectMapper = objectMapper;
        this.eventsPackage = eventsPackage;
        this.zookeeper = zookeeper;
        this.groupId = groupId;
    }

    @Override
    public void saveEvents(String streamName, String aggregateId, Iterable<? extends Event> events, int expectedVersion) {
        KafkaProducer<String, String> producer = initializeProducer(zookeeper);
        try {
            final int[] i = {expectedVersion}; // variable used in lambda should be effectively final

            events.forEach(event -> {
                try {
                    i[0]++;
                    event.version = i[0];

                    String topicName = constructTopicName(streamName, aggregateId);
                    String eventName = event.getClass().getSimpleName();
                    String eventData = objectMapper.writeValueAsString(event);

                    ProducerRecord<String, String> record =
                            new ProducerRecord<>(topicName, eventName, eventData);
                    producer.send(record);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
        } finally {
            producer.close();
        }
    }

    @Override
    public Iterable<? extends Event> getEventsForAggregate(Class<? extends AggregateRoot> aggregate, String aggregateId) {
        String streamName = aggregate.getSimpleName();

        KafkaConsumer<String, String> consumer = initializeConsumer(zookeeper, groupId);
        try {
            String topic = constructTopicName(streamName, aggregateId);

            TopicPartition topicPartition = new TopicPartition(topic, 0);
            consumer.assign(Collections.singletonList(topicPartition));
            consumer.seekToBeginning(Collections.singletonList(topicPartition));

            ConsumerRecords<String, String> records = consumer.poll(0);
            if (records.isEmpty()) {
                records = consumer.poll(Long.MAX_VALUE);

                if (records.isEmpty())
                    throw new AggregateNotFoundException(streamName, ID.fromObject(aggregateId));
            }

            return StreamSupport.stream(records.spliterator(), false).map(record -> {
                Event event = deserializeEvent(record.key(), record.value());
                return event;
            }).collect(Collectors.toList());
        } finally {
            consumer.close();
        }
    }

    private Event deserializeEvent(String key, String value) {
        Event action;
        Optional<? extends Class<?>> clazz;
        ClassPath classpath;
        try {
            classpath = ClassPath.from(getClass().getClassLoader());
            clazz = classpath.getTopLevelClasses(eventsPackage).stream()
                    .filter(s -> s.getSimpleName()
                    .equals(key))
                    .map(s -> s.load())
                    .reduce((aClass, aClass2) -> aClass);

            Class classValue = clazz.orElseThrow(EventNotSupportedExeption::new);
            action = (Event) objectMapper.readValue(value, classValue);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return action;
    }

    private static String constructTopicName(String streamName, String aggregateId) {
        return streamName + "-" + aggregateId;
    }

    private static KafkaProducer<String, String> initializeProducer(String zookeeper) {
        Properties props = new Properties();
        props.put("bootstrap.servers", zookeeper);
        props.put("key.serializer", StringSerializer.class);
        props.put("value.serializer", StringSerializer.class);
        props.put("log.retention.hours", 2147483647);

        return new KafkaProducer<>(props);
    }

    private static KafkaConsumer<String, String> initializeConsumer(String zookeeper, String groupId) {
        Properties props = new Properties();
        props.put("bootstrap.servers", zookeeper);
        props.put("group.id", groupId + EVENTSTORE_CONSUMER_SUFFIX);
        props.put("auto.offset.reset", "earliest");
        props.put("key.deserializer", StringDeserializer.class);
        props.put("value.deserializer", StringDeserializer.class);
        props.put("enable.auto.commit", "false");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");

        return new KafkaConsumer<>(props);
    }

    public static class Builder {
        private String zookeeper;
        private String groupId;
        private ObjectMapper objectMapper;
        private String eventsPackage;

        public Builder withZookeeper(String zookeeper) {
            this.zookeeper = zookeeper;
            return this;
        }

        public Builder withGroupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder withObjectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        public Builder withEventsPackage(String eventsPackage) {
            this.eventsPackage = eventsPackage;
            return this;
        }

        public KafkaEventStore build() {
            checkNotNull(zookeeper);
            checkNotNull(groupId);
            checkNotNull(objectMapper);
            checkNotNull(eventsPackage);

            return new KafkaEventStore(zookeeper, groupId, objectMapper, eventsPackage);
        }
    }
}
