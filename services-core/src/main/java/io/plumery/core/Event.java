package io.plumery.core;

/**
 * Created by ben.goldin on 30/01/2017.
 */
public abstract class Event implements Action {
    public int version;
    public ID id;
}
