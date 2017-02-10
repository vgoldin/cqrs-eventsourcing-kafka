package io.plumery.inventoryitem.core.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.plumery.core.Command;
import io.plumery.core.ID;

public class CreateInventoryItem extends Command {
    public final ID inventoryItemId;
    public final String name;

    @JsonCreator
    public CreateInventoryItem(
            @JsonProperty("inventoryItemId") ID inventoryItemId,
            @JsonProperty("name") String name) {
        this.inventoryItemId = inventoryItemId;
        this.name = name;
    }
}
