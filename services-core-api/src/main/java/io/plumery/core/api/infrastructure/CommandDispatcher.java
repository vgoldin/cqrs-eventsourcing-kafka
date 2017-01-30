package io.plumery.core.api.infrastructure;


import io.plumery.core.api.Command;

public interface CommandDispatcher {
    <T extends Command> void dispatch(T command);
}