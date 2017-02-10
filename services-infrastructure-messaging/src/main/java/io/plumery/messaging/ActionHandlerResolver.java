package io.plumery.messaging;

import com.google.common.collect.Lists;
import io.plumery.core.ActionHandler;
import io.plumery.core.CommandHandler;
import io.plumery.core.EventHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ActionHandlerResolver {
    private static final ThreadLocal<ActionHandlerResolver> CURRENT = new ThreadLocal<>();

    public static ActionHandlerResolver getCurrent() {
        return CURRENT.get();
    }

    public static void setCurrent(ActionHandlerResolver resolver) {
        CURRENT.set(resolver);
    }

    private Set<ActionHandler> actionHandlers;

    private ActionHandlerResolver() {
    }

    public void setActionHandlers(Set<ActionHandler> actionHandlers) {
        this.actionHandlers = actionHandlers;
    }

    public List<ActionHandler> findHandlersFor(String action) {
        List<ActionHandler> list = new ArrayList<>();

        for (ActionHandler actionHandler : actionHandlers) {
            Class<?> actionClass = getHandledActionType(actionHandler.getClass());

            if (actionClass.getSimpleName().equals(action)) {
                list.add(actionHandler);
            }
        }

        return list;
    }

    public Class<?> getHandledActionType(Class<?> clazz) {
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

    public List<String> getSupportedActions() {
        List<String> actions = Lists.newArrayList();
        for (ActionHandler actionHandler : actionHandlers) {
            Class<?> actionClass = getHandledActionType(actionHandler.getClass());
            String action = actionClass.getSimpleName();

            actions.add(action);
        }

        return actions;
    }

    public static ActionHandlerResolver newInstance() {
        ActionHandlerResolver resolver = new ActionHandlerResolver();
        setCurrent(resolver);

        return resolver;
    }
}
