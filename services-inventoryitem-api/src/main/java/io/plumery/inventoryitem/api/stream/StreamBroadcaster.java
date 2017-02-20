package io.plumery.inventoryitem.api.stream;

import io.dropwizard.lifecycle.Managed;

import java.util.Observable;

public abstract class StreamBroadcaster extends Observable
        implements Managed {

    public StreamBroadcaster() {
        super();
    }
}
