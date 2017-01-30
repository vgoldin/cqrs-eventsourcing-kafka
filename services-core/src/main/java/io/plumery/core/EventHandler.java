package io.plumery.core;

public interface EventHandler<T extends Event> extends ActionHandler<T>  {
    public void handle(T event);
}