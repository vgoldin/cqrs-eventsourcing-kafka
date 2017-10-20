package io.plumery.core;

/**
 * Created by ben.goldin on 30/01/2017.
 */
public interface ActionHandler<T extends Action>  {
    public void handle(T action);
}
