package io.plumery.inventoryitem.api.denormalizer.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import io.plumery.core.ActionHandler;
import io.plumery.inventoryitem.api.denormalizer.hazelcast.HazelcastManaged;
import io.plumery.inventoryitem.api.core.EventEnvelope;
import io.plumery.inventoryitem.api.core.InventoryItemListItem;
import io.plumery.inventoryitem.core.events.InventoryItemCreated;
import org.apache.kafka.streams.processor.AbstractProcessor;

public class InventoryItemCreatedHandler extends AbstractProcessor<String, EventEnvelope>
        implements ActionHandler<InventoryItemCreated> {
    private static final String PROJECTION_STATE_STORE = "InventoryItemsProjection";  //FIXME: move to shared constants
    private static final String INVENTORY_ITEMS_MAP = "inventoryItems";

    private final ObjectMapper mapper;
    private final HazelcastInstance hazelcastInstance;

    public InventoryItemCreatedHandler() {
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
    public void handle(InventoryItemCreated event) {
        IMap<String, InventoryItemListItem> inventoryItems = getInventoryItemsMap();

        String id = event.id.toString();

        InventoryItemListItem item = inventoryItems.get(id);
        if (item == null) {
            item = new InventoryItemListItem();
            item.id = id;
            item.name = event.getName();

            inventoryItems.put(id, item);
        }
    }

    private InventoryItemCreated deserializeEvent(EventEnvelope value) {
        InventoryItemCreated event = mapper.convertValue(value.eventData, InventoryItemCreated.class);
        return event;
    }

    private IMap<String, InventoryItemListItem> getInventoryItemsMap() {
        return hazelcastInstance.getMap(INVENTORY_ITEMS_MAP);
    }
}
