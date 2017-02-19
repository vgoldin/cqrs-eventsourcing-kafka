package io.plumery.inventoryitem.api.query;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.*;
import io.plumery.inventoryitem.api.denormalizer.hazelcast.HazelcastManaged;
import io.plumery.inventoryitem.api.core.InventoryItemDetails;
import io.plumery.inventoryitem.api.core.InventoryItemListItem;

import java.util.Collection;

public class InventoryItemsQuery {
    private static final String INVENTORY_ITEMS_MAP = "inventoryItems";
    private static final String INVENTORY_ITEMS_DETAILS_MAP = "inventoryItemDetails";

    public final HazelcastInstance hazelcastInstance;

    public InventoryItemsQuery() {
        this.hazelcastInstance = HazelcastManaged.getInstance();
    }

    public Iterable<InventoryItemListItem> getInventoryItems() {
        IMap<String, InventoryItemListItem> map = hazelcastInstance.getMap(INVENTORY_ITEMS_MAP);

        return map.values();
    }

    public InventoryItemDetails getInventoryItemDetails(String id) {
        IMap<String, InventoryItemDetails> map = hazelcastInstance.getMap(INVENTORY_ITEMS_DETAILS_MAP);

        return map.get(id);
    }

    public Collection<InventoryItemListItem> findInventoryItemsByName(String name) {
        IMap<String, InventoryItemListItem> map = hazelcastInstance.getMap(INVENTORY_ITEMS_MAP);

        return map.values(Predicates.like("name", name));
    }
}
