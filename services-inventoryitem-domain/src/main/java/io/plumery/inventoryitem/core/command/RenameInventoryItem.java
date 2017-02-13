package io.plumery.inventoryitem.core.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.plumery.core.Command;
import io.plumery.core.ID;

public class RenameInventoryItem extends Command {
    public final ID inventoryItemId;
    public final String newName;

    @JsonCreator
    public RenameInventoryItem(
            @JsonProperty("inventoryItemId") ID inventoryItemId,
            @JsonProperty("newName") String newName,
            @JsonProperty("originalVersion") Integer originalVersion) {
        this.inventoryItemId = inventoryItemId;
        this.newName = newName;
        this.originalVersion = originalVersion;
    }
}
