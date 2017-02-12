package io.plumery.inventoryitem.core.domain.event;

import io.plumery.core.Event;
import io.plumery.core.ID;

/**
 * Created by veniamin on 30/01/2017.
 */
public class InventoryItemCreated extends Event {
    public final String name;

    public InventoryItemCreated(ID id, String name) {
        this.id = id;
        this.name = name;
    }
}
