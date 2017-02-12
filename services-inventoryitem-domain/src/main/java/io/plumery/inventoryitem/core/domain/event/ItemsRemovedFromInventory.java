package io.plumery.inventoryitem.core.domain.event;

import io.plumery.core.Event;
import io.plumery.core.ID;

public class ItemsRemovedFromInventory extends Event {
    public final Integer count;

    public ItemsRemovedFromInventory(ID id, Integer count) {
        super();
        this.id = id;
        this.count = count;
    }
}
