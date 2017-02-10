package io.plumery.inventoryitem.core.command.handler;

import io.plumery.core.CommandHandler;
import io.plumery.core.infrastructure.Repository;
import io.plumery.inventoryitem.core.command.DeactivateInventoryItem;
import io.plumery.inventoryitem.core.domain.InventoryItem;

public class DeactivateInventoryItemHandler implements CommandHandler<DeactivateInventoryItem> {
    private Repository<InventoryItem> repository;

    public DeactivateInventoryItemHandler(Repository<InventoryItem> repository) {
        this.repository = repository;
    }

    @Override
    public void handle(DeactivateInventoryItem command) {
        InventoryItem item = repository.getById(command.inventoryItemId);
        item.deactivate();
        repository.save(item, command.originalVersion);
    }
}
