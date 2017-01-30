package io.plumery.messaging.camel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.plumery.core.Event;
import io.plumery.core.infrastructure.EventPublisher;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;

import static io.plumery.messaging.camel.config.MessagingConfig.destinationNameForEventPublishing;
import static io.plumery.messaging.camel.config.MessagingConfig.destinationNameForStreamEventPublishing;

/*
    EventPublisher delegating events to pre-defined Camel route
 */
public class CamelEventPublisher implements EventPublisher {
    private final ProducerTemplate producer;

    public CamelEventPublisher(CamelContext camelContext) {
        try {
            this.producer = camelContext.createProducerTemplate();
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T extends Event> void publish(T event) {
        ObjectMapper mapper = new ObjectMapper();
        String serializedEvent = null;
        try {
            serializedEvent = mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        producer.sendBody(destinationNameForEventPublishing(event.getClass()), serializedEvent);
    }

    @Override
    public <T extends Event> void publish(String streamName, T event) {
        ObjectMapper mapper = new ObjectMapper();
        String serializedEvent = null;
        try {
            serializedEvent = mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        producer.sendBody(destinationNameForEventPublishing(event.getClass()), serializedEvent);
        producer.sendBodyAndHeader(destinationNameForStreamEventPublishing(streamName), serializedEvent, "EVENT_TYPE",
                event.getClass().getSimpleName());
    }
}
