package io.plumery.inventoryitem.core.domain.event;

import io.plumery.core.Event;
import io.plumery.core.ID;

public class InventoryItemRenamed extends Event {
    public final ID id;
    public final String newName;

    public InventoryItemRenamed(ID id, String newName) {
        super();
        this.id = id;
        this.newName = newName;
    }
}
