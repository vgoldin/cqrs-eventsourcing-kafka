package io.plumery.inventoryitem.core.domain;

import io.plumery.core.AggregateRoot;
import io.plumery.core.ID;
import io.plumery.inventoryitem.core.domain.event.InventoryItemDeactivated;
import io.plumery.inventoryitem.core.domain.event.InventoryItemRenamed;
import io.plumery.inventoryitem.core.domain.event.ItemsCheckedInToInventory;
import io.plumery.inventoryitem.core.domain.event.ItemsRemovedFromInventory;
import io.plumery.inventoryitem.core.events.InventoryItemCreated;

import static com.google.common.base.Strings.isNullOrEmpty;

public class InventoryItem extends AggregateRoot {
    private Boolean activated;
    private String name;

    @SuppressWarnings("unused")
    private InventoryItem() {}

    public InventoryItem(ID id, String name) {
        InventoryItemCreated event = new InventoryItemCreated()
            .withName(name);
        event.id = id;

        applyChange(event);
    }


    public void changeName(String newName) {
        if (isNullOrEmpty(newName)) throw new IllegalArgumentException("newName");
        applyChange(new InventoryItemRenamed(id, newName));
    }

    public void remove(Integer count) {
        if (count <= 0) throw new IllegalStateException("cant remove negative count from inventory");
        applyChange(new ItemsRemovedFromInventory(id, count));
    }


    public void checkIn(Integer count) {
        if (count <= 0) throw new IllegalStateException("must have a count greater than 0 to add to inventory");
        applyChange(new ItemsCheckedInToInventory(id, count));
    }

    public void deactivate() {
        if(!activated) throw new IllegalStateException("already deactivated");
        applyChange(new InventoryItemDeactivated(id));
    }


    private void apply(InventoryItemRenamed e) {
        name = e.newName;
    }

    private void apply(InventoryItemCreated e) {
        id = e.id;
        activated = true;
    }

    private void apply(InventoryItemDeactivated e) {
        activated = false;
    }
}
