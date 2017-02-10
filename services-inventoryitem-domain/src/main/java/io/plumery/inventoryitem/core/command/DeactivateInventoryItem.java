package io.plumery.inventoryitem.core.command;

import io.plumery.core.Command;
import io.plumery.core.ID;

public class DeactivateInventoryItem extends Command {
    public final ID inventoryItemId;

    public DeactivateInventoryItem(ID inventoryItemId, Integer originalVersion) {
        this.inventoryItemId = inventoryItemId;
        this.originalVersion = originalVersion;
    }
}
