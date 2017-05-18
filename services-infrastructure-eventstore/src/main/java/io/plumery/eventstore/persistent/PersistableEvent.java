package io.plumery.eventstore.persistent;

import java.util.UUID;

/**
 * Created by veniamin on 18/05/2017.
 */
public class PersistableEvent {
    public final String eventType;
    public final byte[] data;
    public final UUID uuid;

    public PersistableEvent(UUID uuid, String eventType, byte[] data) {
        this.eventType = eventType;
        this.uuid = uuid;
        this.data = data.clone();
    }
}
