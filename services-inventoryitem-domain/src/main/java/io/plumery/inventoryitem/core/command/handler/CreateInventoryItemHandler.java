package io.plumery.inventoryitem.core.command.handler;

import io.plumery.core.CommandHandler;
import io.plumery.core.infrastructure.Repository;
import io.plumery.inventoryitem.core.command.CreateInventoryItem;
import io.plumery.inventoryitem.core.domain.InventoryItem;

public class CreateInventoryItemHandler implements CommandHandler<CreateInventoryItem> {
    private Repository<InventoryItem> repository;

    public CreateInventoryItemHandler(Repository<InventoryItem> repository) {
        this.repository = repository;
    }

    @Override
    public void handle(CreateInventoryItem command) {
        InventoryItem item = new InventoryItem(command.inventoryItemId, command.name);
        repository.save(item, -1);
    }
}
