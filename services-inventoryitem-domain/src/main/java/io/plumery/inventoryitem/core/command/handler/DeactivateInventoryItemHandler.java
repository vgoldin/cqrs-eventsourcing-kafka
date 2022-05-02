package io.plumery.inventoryitem.core.command.handler;

import io.plumery.core.CommandHandler;
import io.plumery.core.ID;
import io.plumery.core.infrastructure.Repository;
import io.plumery.inventoryitem.core.commands.DeactivateInventoryItem;
import io.plumery.inventoryitem.core.domain.InventoryItem;

public class DeactivateInventoryItemHandler implements CommandHandler<DeactivateInventoryItem> {
    private final Repository<InventoryItem> repository;

    public DeactivateInventoryItemHandler(Repository<InventoryItem> repository) {
        this.repository = repository;
    }

    @Override
    public void handle(DeactivateInventoryItem command) {
        InventoryItem item = repository.getById(ID.fromObject(command.getInventoryItemId()));
        item.deactivate();
        repository.save(item, command.originalVersion);
    }
}
