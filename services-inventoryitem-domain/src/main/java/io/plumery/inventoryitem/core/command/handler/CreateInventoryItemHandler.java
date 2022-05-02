package io.plumery.inventoryitem.core.command.handler;

import io.plumery.core.CommandHandler;
import io.plumery.core.ID;
import io.plumery.core.infrastructure.Repository;
import io.plumery.inventoryitem.core.commands.CreateInventoryItem;
import io.plumery.inventoryitem.core.domain.InventoryItem;

public class CreateInventoryItemHandler implements CommandHandler<CreateInventoryItem> {
    private final Repository<InventoryItem> repository;

    public CreateInventoryItemHandler(Repository<InventoryItem> repository) {
        this.repository = repository;
    }

    @Override
    public void handle(CreateInventoryItem command) {
        InventoryItem item = new InventoryItem(ID.fromObject(command.getInventoryItemId()),
                command.getName());
        repository.save(item, -1);
    }
}
