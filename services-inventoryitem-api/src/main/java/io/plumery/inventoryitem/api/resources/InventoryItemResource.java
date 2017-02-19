package io.plumery.inventoryitem.api.resources;

import io.plumery.core.infrastructure.CommandDispatcher;
import io.plumery.inventoryitem.api.core.InventoryItem;
import io.plumery.inventoryitem.api.core.InventoryItemListItem;
import io.plumery.inventoryitem.api.query.InventoryItemsQuery;
import io.plumery.inventoryitem.core.commands.CreateInventoryItem;
import io.plumery.inventoryitem.core.commands.RenameInventoryItem;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;

@Path("/inventory-items")
@Produces(MediaType.APPLICATION_JSON)
public class InventoryItemResource {
    private final InventoryItemsQuery query;
    private final CommandDispatcher dispatcher;

    public InventoryItemResource(InventoryItemsQuery query, CommandDispatcher dispatcher) {
        this.query = query;
        this.dispatcher = dispatcher;
    }

    @GET
    public Iterable<InventoryItemListItem> inventoryItems() {
        return query.getInventoryItems();
    }

    @POST
    public Response create(InventoryItem inventoryItem) {
        CreateInventoryItem command = new CreateInventoryItem()
            .withInventoryItemId(inventoryItem.inventoryItemId)
            .withName(inventoryItem.name);

        dispatcher.dispatch(command);

        return Response.status(202).build();
    }

    @PUT
    public Response rename(InventoryItem inventoryItem) {
        RenameInventoryItem command = new RenameInventoryItem()
                .withInventoryItemId(inventoryItem.inventoryItemId)
                .withNewName(inventoryItem.name);

        command.originalVersion = inventoryItem.version;

        dispatcher.dispatch(command);

        return Response.status(202).build();
    }

    @GET
    @Path("/errors")
    public Iterable<Object> errors() {
        return Collections.emptyList();
    }
}
