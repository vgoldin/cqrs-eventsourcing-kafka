package io.plumery.inventoryitem.api.denormalizer.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import io.plumery.core.ActionHandler;
import io.plumery.inventoryitem.api.core.EventEnvelope;
import io.plumery.inventoryitem.api.core.InventoryItemListItem;
import io.plumery.inventoryitem.api.denormalizer.hazelcast.HazelcastManaged;
import io.plumery.inventoryitem.core.events.InventoryItemDeactivated;
import io.plumery.inventoryitem.core.events.InventoryItemRenamed;
import org.apache.kafka.streams.processor.AbstractProcessor;

public class InventoryItemDeactivatedHandler extends AbstractProcessor<String, EventEnvelope>
        implements ActionHandler<InventoryItemDeactivated> {
    private static final String INVENTORY_ITEMS_MAP = "inventoryItems";

    private final ObjectMapper mapper;
    private final HazelcastInstance hazelcastInstance;

    public InventoryItemDeactivatedHandler() {
        this.mapper = new ObjectMapper();
        this.hazelcastInstance = HazelcastManaged.getInstance();
    }

    @Override
    public void process(String key, EventEnvelope value) {
        handle(deserializeEvent(value));

        context().forward(key, value);
        context().commit();
    }

    @Override
    public void handle(InventoryItemDeactivated event) {
        IMap<String, InventoryItemListItem> inventoryItems = getInventoryItemsMap();

        String id = event.id.toString();

        InventoryItemListItem item = inventoryItems.get(id);
        if (item != null) {
            item.active = false;
        }

        inventoryItems.put(id, item);
    }

    private InventoryItemDeactivated deserializeEvent(EventEnvelope value) {
        InventoryItemDeactivated event = mapper.convertValue(value.eventData, InventoryItemDeactivated.class);
        return event;
    }

    private IMap<String, InventoryItemListItem> getInventoryItemsMap() {
        return hazelcastInstance.getMap(INVENTORY_ITEMS_MAP);
    }
}
