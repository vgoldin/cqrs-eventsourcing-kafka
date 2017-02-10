package io.plumery.messaging.local;

import io.plumery.core.ActionHandler;
import io.plumery.core.Command;
import io.plumery.core.infrastructure.CommandDispatcher;
import io.plumery.messaging.ActionHandlerResolver;

import java.util.List;

public class LocalCommandDispatcher implements CommandDispatcher {
    private final ActionHandlerResolver resolverProvider;

    public LocalCommandDispatcher(ActionHandlerResolver resolverProvider) {
        this.resolverProvider = resolverProvider;
    }

    @Override
    public <T extends Command> void dispatch(T command) {
        List<ActionHandler> handlers = resolverProvider.findHandlersFor(command.getClass().getSimpleName());

        if(handlers != null) {
            for(ActionHandler<T> handler : handlers) {
                handler.handle(command);
            }
        }
    }
}
