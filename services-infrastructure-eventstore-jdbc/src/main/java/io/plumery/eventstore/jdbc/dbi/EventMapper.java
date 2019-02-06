package io.plumery.eventstore.jdbc.dbi;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.plumery.core.Event;
import io.plumery.core.exception.SystemException;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class EventMapper implements ResultSetMapper<Event> {
    private final ObjectMapper objectMapper;

    public EventMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Event map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        String type = resultSet.getString("type");
        Event event;
        try {
            JavaType t = TypeFactory.defaultInstance().constructFromCanonical(type);
            event = objectMapper.readValue(resultSet.getBytes("data"), t);

        } catch (JsonParseException e) {
            throw new SystemException(UUID.randomUUID().toString(), e);
        } catch (JsonMappingException e) {
            throw new SystemException(UUID.randomUUID().toString(), e);
        } catch (IOException e) {
            throw new SystemException(UUID.randomUUID().toString(), e);
        }

        return event;
    }
}
