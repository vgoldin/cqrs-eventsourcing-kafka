package io.plumery.messaging.camel.config;

import io.plumery.core.Command;
import io.plumery.core.Event;

/**
 * Created by ben.goldin on 30/01/2017.
 */
public class MessagingConfig {
    public static String destinationNameForStreamEventPublishing(String streamName) {
        return "activemq:topic:VirtualTopic.PlumeryDemo." + streamName;
    }

    public static String destinationNameForEventPublishing(Class<? extends Event> eventType) {
        return "activemq:topic:VirtualTopic.PlumeryDemo." + eventType.getSimpleName();
    }

    public static String destinationNameForEventConsuming(Class<? extends Event> eventType, String applicationName) {
        return destinationNameForEventConsuming(eventType.getSimpleName(), applicationName);
    }

    public static String destinationNameForEventConsuming(String eventTypeName, String applicationName) {
        return "activemq:queue:Consumer." + applicationName + ".VirtualTopic.PlumeryDemo." + eventTypeName;
    }

    public static String destinationNameForCommand(Class<? extends Command> commandType) {
        return "activemq:queue:PlumeryDemo." + commandType.getSimpleName();
    }

    public static String destinationNameForCommand(String commandType) {
        return "activemq:queue:PlumeryDemo." + commandType;
    }
}
