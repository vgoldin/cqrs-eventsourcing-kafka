package io.plumery.core;

/**
 * The role interface representing a command
 */
public abstract class Command implements Action {
    public int originalVersion;
    public ID id;
}