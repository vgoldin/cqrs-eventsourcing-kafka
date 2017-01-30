package io.plumery.core.infrastructure;


import io.plumery.core.Command;

public interface CommandDispatcher {
    <T extends Command> void dispatch(T command);
}