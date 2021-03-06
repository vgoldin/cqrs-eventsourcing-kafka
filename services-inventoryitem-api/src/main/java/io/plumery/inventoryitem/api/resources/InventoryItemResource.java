package io.plumery.inventoryitem.api.resources;

import io.plumery.core.infrastructure.CommandDispatcher;
import io.plumery.inventoryitem.api.core.EventEnvelope;
import io.plumery.inventoryitem.api.core.InventoryItem;
import io.plumery.inventoryitem.api.core.InventoryItemListItem;
import io.plumery.inventoryitem.api.query.InventoryItemsQuery;
import io.plumery.inventoryitem.api.stream.StreamBroadcaster;
import io.plumery.inventoryitem.core.commands.CreateInventoryItem;
import io.plumery.inventoryitem.core.commands.DeactivateInventoryItem;
import io.plumery.inventoryitem.core.commands.RenameInventoryItem;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseBroadcaster;
import org.glassfish.jersey.media.sse.SseFeature;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Observable;
import java.util.Observer;

@Api(value = "inventory-items", description = "Endpoint for inventory items management")
@Path("/inventory-items")
@Produces(MediaType.APPLICATION_JSON)
public class InventoryItemResource implements Observer {
    private static final SseBroadcaster BROADCASTER = new SseBroadcaster();
    private final InventoryItemsQuery query;
    private final CommandDispatcher dispatcher;

    public InventoryItemResource(InventoryItemsQuery query, CommandDispatcher dispatcher) {
        this.query = query;
        this.dispatcher = dispatcher;
    }

    @GET
    @ApiOperation(value = "Returns inventory items", notes = "Returns a complete list of inventory items.",
            response = InventoryItemListItem.class,
            responseContainer = "List")
    public Iterable<InventoryItemListItem> inventoryItems() {
        return query.getInventoryItems();
    }

    @POST
    @ApiOperation(value = "Create new inventory item", notes = "Create a new inventory item")
    public Response create(InventoryItem inventoryItem) {
        CreateInventoryItem command = new CreateInventoryItem()
            .withInventoryItemId(inventoryItem.inventoryItemId)
            .withName(inventoryItem.name);

        dispatcher.dispatch(command);

        return Response.status(202).build();
    }

    @PUT
    @Path("/{id}")
    @ApiOperation(value = "Rename inventory item", notes = "Assign a new name to the inventory item")
    public Response rename(@PathParam("id") String id, InventoryItem inventoryItem) {
        RenameInventoryItem command = new RenameInventoryItem()
                .withInventoryItemId(id)
                .withNewName(inventoryItem.name);

        command.originalVersion = inventoryItem.version;

        dispatcher.dispatch(command);

        return Response.status(202).build();
    }

    @POST
    @Path("/{id}/deactivate")
    @ApiOperation(value = "Deactivate inventory item", notes = "Deactivate the inventory item so that it would not appear in the list again")
    public Response deactivate(@PathParam("id") String id, InventoryItem inventoryItem) {
        DeactivateInventoryItem command = new DeactivateInventoryItem()
                .withInventoryItemId(id);

        command.originalVersion = inventoryItem.version;
        dispatcher.dispatch(command);

        return Response.status(202).build();
    }

    @GET
    @Path("/events.stream")
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    @ApiOperation(value = "Get Event Stream of Application Events", notes = "Returns a continuous stream of application events using Server-Sent Events.")
    public EventOutput errors() {
        final EventOutput eventOutput = new EventOutput();
        BROADCASTER.add(eventOutput);

        return eventOutput;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof StreamBroadcaster && arg != null) {
            EventEnvelope e = (EventEnvelope) arg;
            OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
            OutboundEvent event = eventBuilder
                    .mediaType(MediaType.APPLICATION_JSON_TYPE)
                    .id(e.eventId.orElse(null))
                    .name(e.eventType)
                    .data(e.eventData)
                    .build();

            BROADCASTER.broadcast(event);
        }
    }
}
