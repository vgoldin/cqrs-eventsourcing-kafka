package io.plumery.inventoryitem.api.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InventoryItem {
    public final String inventoryItemId;
    public final String name;
    public final Integer version;

    @JsonCreator
    public InventoryItem(
            @JsonProperty("inventoryItemId") String inventoryItemId,
            @JsonProperty("name") String name,
            @JsonProperty("version") Integer version) {
        this.inventoryItemId = inventoryItemId;
        this.name = name;
        this.version = version;
    }
}
