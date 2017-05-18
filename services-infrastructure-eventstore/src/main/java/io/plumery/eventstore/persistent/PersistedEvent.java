package io.plumery.eventstore.persistent;

import java.util.UUID;

/**
 * Created by veniamin on 18/05/2017.
 */
public class PersistedEvent {
    public final UUID streamId;
    public final UUID id;
    public final int version;
    public final String type;
    public final byte[] data;

    public PersistedEvent(UUID streamId, UUID id, int version, String type, byte[] data){
        this.streamId = streamId;
        this.id = id;
        this.version = version;
        this.type = type;
        this.data = data.clone();
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof PersistedEvent) && ((PersistedEvent) other).id.equals(id);
    }

    @Override
    public String toString() {
        return String.format("%s event #%d in stream %s (event id %s)",
                type, version, streamId, id);
    }
}
