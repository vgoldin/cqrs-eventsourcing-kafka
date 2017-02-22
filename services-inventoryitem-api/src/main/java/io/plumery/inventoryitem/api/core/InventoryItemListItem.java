package io.plumery.inventoryitem.api.core;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel
public class InventoryItemListItem implements Serializable {
    @ApiModelProperty("The id of the inventory item")
    public String id;
    @ApiModelProperty("The name of the inventory item")
    public String name;
    @ApiModelProperty("The current version of the inventory item")
    public int version;
}
