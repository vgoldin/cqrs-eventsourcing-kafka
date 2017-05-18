package io.plumery.eventstore.jdbc.dbi;

import io.plumery.core.Event;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface EventStreams {
    @SqlQuery("select e.stream_id as streamId, e.event_id as id, e.data, e.version, e.type " +
            "from events as e join streams as s on s.id=e.stream_id where s.id = :streamId")
    Iterable<Event> loadEvents(@Bind("streamId") String streamId);

    @SqlQuery("select version from streams where id = :streamId")
    Integer getCurrentStreamVersion(@Bind("streamId") String streamId);

    @SqlUpdate("insert into streams (id, type) values (:streamId, :type)")
    int createNewStream(@Bind("type") String type, @Bind("streamId") String streamId);


    @SqlUpdate("insert into events (event_id, data, type, version, stream_id) values (:eventId, :data, :type, :version, :streamId)")
    void appendNewEvent(@Bind("streamId") String streamId,
                        @Bind("eventId") String eventId,
                        @Bind("data") byte[] data,
                        @Bind("type") String type,
                        @Bind("version") int version);

    @SqlUpdate("update streams set version = :version where id = :streamId")
    void setCurrentStreamVersion(@Bind("streamId") String streamId, @Bind("version") int version);
}
