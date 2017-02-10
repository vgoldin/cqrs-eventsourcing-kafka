package io.plumery.inventoryitem.core.command.handler;

import io.plumery.core.CommandHandler;
import io.plumery.core.infrastructure.Repository;
import io.plumery.inventoryitem.core.command.RemoveItemsFromInventory;
import io.plumery.inventoryitem.core.domain.InventoryItem;

public class RemoveItemsFromInventoryHandler implements CommandHandler<RemoveItemsFromInventory> {
    private Repository<InventoryItem> repository;

    public RemoveItemsFromInventoryHandler(Repository<InventoryItem> repository) {
        this.repository = repository;
    }

    @Override
    public void handle(RemoveItemsFromInventory command) {
        InventoryItem item = repository.getById(command.inventoryItemId);
        item.remove(command.count);
        repository.save(item, command.originalVersion);
    }
}
