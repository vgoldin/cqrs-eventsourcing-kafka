package io.plumery.core;

import java.io.Serializable;
import java.util.UUID;

/**
 * The class representing a default Identity of the AggregateRoot
 *
 * @author V.Goldin
 */
public class ID implements Serializable {
    private final String id;

    private ID(String id) {
        this.id = id;
    }

    public static ID fromObject(Object id) {
        if (id instanceof String) {
            return new ID((String) id);
        } else if (id instanceof UUID) {
            return new ID(id.toString());
        } else {
            new IllegalArgumentException("The id should be of either String or UUID type");
        }

        return null;
    }

    public String toString() {
        return id;
    }
}