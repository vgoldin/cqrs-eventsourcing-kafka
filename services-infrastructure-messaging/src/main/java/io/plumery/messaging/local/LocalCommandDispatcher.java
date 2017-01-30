package io.plumery.messaging.local;

import io.plumery.core.ActionHandler;
import io.plumery.core.Command;
import io.plumery.core.infrastructure.CommandDispatcher;

import java.util.List;

public class LocalCommandDispatcher implements CommandDispatcher {
    private final LocalActionHandlerResolver resolverProvider;

    public LocalCommandDispatcher(LocalActionHandlerResolver resolverProvider) {
        this.resolverProvider = resolverProvider;
    }

    @Override
    public <T extends Command> void dispatch(T command) {
        List<ActionHandler<T>> handlers = resolverProvider.findHandlersFor(command);

        if(handlers != null) {
            for(ActionHandler<T> handler : handlers) {
                handler.handle(command);
            }
        }
    }
}
