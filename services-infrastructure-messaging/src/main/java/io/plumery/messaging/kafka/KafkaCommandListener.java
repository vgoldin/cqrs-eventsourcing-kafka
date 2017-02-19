package io.plumery.messaging.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.lifecycle.Managed;
import io.plumery.core.Action;
import io.plumery.core.ActionHandler;
import io.plumery.core.infrastructure.CommandListener;
import io.plumery.messaging.ActionHandlerResolver;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class KafkaCommandListener implements CommandListener, Managed {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaCommandListener.class);

    private final KafkaConsumer consumer;
    private final ActionHandlerResolver resolver;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final ObjectMapper objectMapper;

    public KafkaCommandListener(String zookeeper, String groupId, ObjectMapper objectMapper) {
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
    }

    @Override
    public void start() throws Exception {
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
                    try {
                        String action = record.topic().replace(Constants.COMMAND_TOPIC_PREFIX, "");
                        handleAction(action, record.value());

                        consumer.commitSync();
                    } catch (Exception ex) {
                        LOG.error("Error handling the record", ex);
                    }
                }
            }
        } catch (WakeupException e) {
            if (!closed.get()) throw e;
        } finally {
            consumer.close();
        }
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
