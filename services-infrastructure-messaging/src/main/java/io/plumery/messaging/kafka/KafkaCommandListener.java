package io.plumery.messaging.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.lifecycle.Managed;
import io.plumery.core.Action;
import io.plumery.core.ActionHandler;
import io.plumery.core.exception.ApplicationException;
import io.plumery.core.exception.SystemException;
import io.plumery.core.infrastructure.CommandListener;
import io.plumery.core.infrastructure.EventPublisher;
import io.plumery.messaging.ActionHandlerResolver;
import io.plumery.messaging.utils.EventUtils;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class KafkaCommandListener implements CommandListener, Managed {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaCommandListener.class);
    public static final String APPLICATION_EVENTS = ".ApplicationEvents";

    private final KafkaConsumer consumer;
    private final ActionHandlerResolver resolver;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final ObjectMapper objectMapper;
    private final EventPublisher applicationEventPublisher;
    private final String aggregateRootName;
    private final String applicationEventsStream;

    public KafkaCommandListener(String zookeeper, String groupId, ObjectMapper objectMapper,
                                EventPublisher applicationEventPublisher,
                                String aggregateRootName) {
        resolver = ActionHandlerResolver.getCurrent();

        Properties props = new Properties();
        props.put("bootstrap.servers", zookeeper);
        props.put("group.id", groupId);
        props.put("key.deserializer", StringDeserializer.class);
        props.put("value.deserializer", StringDeserializer.class);
        props.put("enable.auto.commit", "false");
        props.put("auto.offset.reset", "earliest");

        this.consumer = new KafkaConsumer(props);
        this.objectMapper = objectMapper;
        this.applicationEventPublisher = applicationEventPublisher;
        this.aggregateRootName = aggregateRootName;

        this.applicationEventsStream = aggregateRootName + APPLICATION_EVENTS;
    }

    @Override
    public void start() throws Exception {
        new Thread(() -> {
            try {
                List<String> actionTopics = resolver.getSupportedActions().stream()
                        .map(s -> Constants.COMMAND_TOPIC_PREFIX + s)
                        .collect(Collectors.toList());

                consumer.subscribe(actionTopics);
                LOG.info("Subscribed for [" + actionTopics + "]");

                while (!closed.get()) {
                    ConsumerRecords<String, String> records = consumer.poll(10000);
                    for (ConsumerRecord<String, String> record : records) {
                        LOG.debug("Received record [" + record + "] from [" + record.topic() + "]");
                        String action = record.topic().replace(Constants.COMMAND_TOPIC_PREFIX, "");
                        try {
                            LOG.debug("Handling action [" + action + "]");
                            handleAction(action, record.value());

                            consumer.commitSync();
                        } catch (Exception ex) {
                            handleException(action, ex);
                        }
                    }
                }
            } catch (WakeupException e) {
                if (!closed.get()) throw e;
            } finally {
                consumer.close();
            }
        }).start();
    }

    private void handleException(String action, Exception ex) {
        Exception targetException;
        if (!(ex instanceof ApplicationException)) {
            String errorEventId = UUID.randomUUID().toString();
            targetException = new SystemException(errorEventId, ex);

            LOG.error("Error handling the record. Error Id: [" +errorEventId+"]", targetException);
        } else {
            targetException = ex;
            ((ApplicationException) targetException).setAction(action);

            LOG.debug("Application error while handling the action: [" +action+"]", targetException);
        }

        applicationEventPublisher.publish(applicationEventsStream, EventUtils.exceptionToEvent(targetException));
    }

    private void handleAction(String action, String value) {
        List<ActionHandler> handlers = resolver.findHandlersFor(action);
        Class<?> clazz = resolver.getHandledActionType(handlers.get(0).getClass());
        Action actionClazz;

        try {
            actionClazz = (Action) objectMapper.readValue(value, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (ActionHandler handler : handlers) {
            handler.handle(actionClazz);
        }
    }

    @Override
    public void stop() throws Exception {
        closed.set(true);
        consumer.wakeup();
    }
}
