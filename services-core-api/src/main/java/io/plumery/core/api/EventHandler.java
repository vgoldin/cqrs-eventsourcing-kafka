package io.plumery.core.api;

public interface EventHandler<T extends Event> extends ActionHandler<T>  {
    public void handle(T event);
}