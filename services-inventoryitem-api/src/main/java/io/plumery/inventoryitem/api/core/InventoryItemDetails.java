package io.plumery.inventoryitem.api.core;

/**
 * Created by veniamin on 18/02/2017.
 */
public class InventoryItemDetails {
    public final String id;
    public final String name;
    public final int currentCount;
    public final int version;

    public InventoryItemDetails(String id, String name, int currentCount, int version) {
        this.id = id;
        this.name = name;
        this.currentCount = currentCount;
        this.version = version;
    }
}
