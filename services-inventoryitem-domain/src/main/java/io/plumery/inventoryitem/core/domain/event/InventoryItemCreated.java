package io.plumery.inventoryitem.core.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.plumery.core.Event;
import io.plumery.core.ID;

public class InventoryItemCreated extends Event {
    public final String name;

    @JsonCreator
    public InventoryItemCreated(
            @JsonProperty("id") ID id,
            @JsonProperty("name") String name,
            @JsonProperty("version") Integer version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }
}
