package io.plumery.messaging.local;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import io.plumery.core.ActionHandler;
import io.plumery.core.Event;
import io.plumery.core.infrastructure.EventPublisher;
import io.plumery.messaging.ActionHandlerResolver;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class LocalEventPublisher implements EventPublisher {
    private final ActionHandlerResolver resolverProvider;

    /**
     * Inject lazy LocalActionHandlerResolver provider in order to be able using EventPublisher in action handlers (directly or indirectly);
     * @param resolverProvider
     */
    public LocalEventPublisher(ActionHandlerResolver resolverProvider) {
        this.resolverProvider = resolverProvider;
    }

    @Override
    public <T extends Event> void publish(String streamName, T event) {
        List<ActionHandler> handlers = resolverProvider.findHandlersFor(event.getClass().getSimpleName());

        if(handlers != null && handlers.size() > 0) {
            for (ActionHandler<T> handler : handlers) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    Method handleMethod = getHandlingMethod(handler);
                    Class expectedEventType
                            = handleMethod.getParameterTypes()[0];
                    handleMethod.setAccessible(true);
                    handleMethod.invoke(handler, mapper.readValue(mapper.writeValueAsString(event), expectedEventType));
                } catch (NoSuchMethodException | IOException | InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private <T extends Event> Method getHandlingMethod(ActionHandler<T> handler) throws NoSuchMethodException {
        for (Method method: handler.getClass().getDeclaredMethods()) {
            if ((method.getParameterTypes().length > 0)
                    && Event.class.equals(TypeToken.of(method.getParameterTypes()[0]).getRawType().getSuperclass())) {
                return method;
            }
        }
        return null;
    }
}
