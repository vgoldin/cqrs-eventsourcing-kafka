package io.plumery.eventstore.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.plumery.core.ID;

import java.io.IOException;

public class IDSerializer extends StdSerializer<ID> {

    public IDSerializer() {
        this(null);
    }

    public IDSerializer(Class<ID> t) {
        super(t);
    }

    @Override
    public void serialize(ID value, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
        jgen.writeString(value.toString());
    }
}
