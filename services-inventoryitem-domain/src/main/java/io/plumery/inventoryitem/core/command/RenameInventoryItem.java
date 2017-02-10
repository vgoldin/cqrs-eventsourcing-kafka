package io.plumery.inventoryitem.core.command;

import io.plumery.core.Command;
import io.plumery.core.ID;

public class RenameInventoryItem extends Command {
    public final ID inventoryItemId;
    public final String newName;

    public RenameInventoryItem(ID inventoryItemId, String newName, Integer originalVersion) {
        this.inventoryItemId = inventoryItemId;
        this.newName = newName;
        this.originalVersion = originalVersion;
    }
}
