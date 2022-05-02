package io.plumery.inventoryitem.core.command.handler;

import io.plumery.core.CommandHandler;
import io.plumery.core.ID;
import io.plumery.core.infrastructure.Repository;
import io.plumery.inventoryitem.core.commands.RenameInventoryItem;
import io.plumery.inventoryitem.core.domain.InventoryItem;

public class RenameInventoryCommandHandler implements CommandHandler<RenameInventoryItem> {
    private final Repository<InventoryItem> repository;

    public RenameInventoryCommandHandler(Repository<InventoryItem> repository) {
        this.repository = repository;
    }

    @Override
    public void handle(RenameInventoryItem command) {
        InventoryItem item = repository.getById(ID.fromObject(command.getInventoryItemId()));
        item.changeName(command.getNewName());
        repository.save(item, command.originalVersion);
    }
}
