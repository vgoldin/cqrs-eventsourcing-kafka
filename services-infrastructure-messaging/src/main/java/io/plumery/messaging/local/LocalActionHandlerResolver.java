package io.plumery.messaging.local;

import io.plumery.core.Action;
import io.plumery.core.ActionHandler;
import io.plumery.core.CommandHandler;
import io.plumery.core.EventHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LocalActionHandlerResolver {
    private final Set<ActionHandler> actionHandlers;

    public LocalActionHandlerResolver(Set<ActionHandler> actionHandlers) {
        this.actionHandlers = actionHandlers;
    }

    public <T extends Action> List<ActionHandler<T>> findHandlersFor(T action) {
        List<ActionHandler<T>> list = new ArrayList<>();

        for (ActionHandler actionHandler : actionHandlers) {
            Class<?> actionClass = getHandledActionType(actionHandler.getClass());

            if (actionClass.getSimpleName().equals(action.getClass().getSimpleName())) {
                list.add(actionHandler);
            }
        }

        return list;
    }

    private Class<?> getHandledActionType(Class<?> clazz) {
        Type[] genericInterfaces = clazz.getGenericInterfaces();

        ParameterizedType type = findByRawType(genericInterfaces, CommandHandler.class);
        if (type == null) {
            type = findByRawType(genericInterfaces, EventHandler.class);
        }

        return (Class<?>) type.getActualTypeArguments()[0];
    }

    private ParameterizedType findByRawType(Type[] genericInterfaces, Class<?> expectedRawType) {
        for (Type type : genericInterfaces) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parametrized = (ParameterizedType) type;

                if (expectedRawType.equals(parametrized.getRawType())) {
                    return parametrized;
                }
            }
        }

        return null;
    }

}
