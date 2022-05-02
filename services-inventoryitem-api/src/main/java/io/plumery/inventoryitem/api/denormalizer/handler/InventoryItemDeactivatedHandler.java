package io.plumery.inventoryitem.api.denormalizer.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import io.plumery.core.ActionHandler;
import io.plumery.inventoryitem.api.core.EventEnvelope;
import io.plumery.inventoryitem.api.core.InventoryItemListItem;
import io.plumery.inventoryitem.api.denormalizer.Constant;
import io.plumery.inventoryitem.api.denormalizer.hazelcast.HazelcastManaged;
import io.plumery.inventoryitem.core.events.InventoryItemDeactivated;
import org.apache.kafka.streams.processor.AbstractProcessor;

public class InventoryItemDeactivatedHandler extends AbstractProcessor<String, EventEnvelope>
        implements ActionHandler<InventoryItemDeactivated> {
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
        inventoryItems.delete(id);
    }

    private InventoryItemDeactivated deserializeEvent(EventEnvelope value) {
        return mapper.convertValue(value.eventData, InventoryItemDeactivated.class);
    }

    private IMap<String, InventoryItemListItem> getInventoryItemsMap() {
        return hazelcastInstance.getMap(Constant.INVENTORY_ITEMS_MAP);
    }
}
