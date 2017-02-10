package io.plumery.inventoryitem.core.infrastructure;

import io.plumery.core.infrastructure.EventStore;
import io.plumery.eventstore.EventStoreAwareRepository;
import io.plumery.inventoryitem.core.domain.InventoryItem;

public class InventoryItemRepository extends EventStoreAwareRepository<InventoryItem> {
    public InventoryItemRepository(EventStore store) {
        this.store = store;
    }
}
