package io.plumery.inventoryitem.api.denormalizer.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import io.plumery.core.ActionHandler;
import io.plumery.inventoryitem.api.core.EventEnvelope;
import io.plumery.inventoryitem.api.core.InventoryItemListItem;
import io.plumery.inventoryitem.api.denormalizer.Constant;
import io.plumery.inventoryitem.api.denormalizer.hazelcast.HazelcastManaged;
import io.plumery.inventoryitem.core.events.InventoryItemCreated;
import io.plumery.inventoryitem.core.events.InventoryItemRenamed;
import org.apache.kafka.streams.processor.AbstractProcessor;

public class InventoryItemRenamedHandler extends AbstractProcessor<String, EventEnvelope>
        implements ActionHandler<InventoryItemRenamed> {
    private final ObjectMapper mapper;
    private final HazelcastInstance hazelcastInstance;

    public InventoryItemRenamedHandler() {
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
    public void handle(InventoryItemRenamed event) {
        IMap<String, InventoryItemListItem> inventoryItems = getInventoryItemsMap();

        String id = event.id.toString();

        InventoryItemListItem item = inventoryItems.get(id);
        if (item != null) {
            item.name = event.getNewName();
            item.version = event.version;
            inventoryItems.put(id, item);
        }
    }

    private InventoryItemRenamed deserializeEvent(EventEnvelope value) {
        InventoryItemRenamed event = mapper.convertValue(value.eventData, InventoryItemRenamed.class);
        return event;
    }

    private IMap<String, InventoryItemListItem> getInventoryItemsMap() {
        return hazelcastInstance.getMap(Constant.INVENTORY_ITEMS_MAP);
    }
}
