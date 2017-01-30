package io.plumery.core;

/**
 * The interface representing a command handler
 */
public interface CommandHandler <T extends Command> extends ActionHandler<T> {
    public void handle(T command);
}
