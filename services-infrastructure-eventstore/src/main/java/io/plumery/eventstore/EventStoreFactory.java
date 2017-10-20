package io.plumery.eventstore;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.migrations.CloseableLiquibase;
import io.dropwizard.migrations.CloseableLiquibaseWithClassPathMigrationsFile;
import io.dropwizard.setup.Environment;
import io.plumery.core.infrastructure.EventPublisher;
import io.plumery.core.infrastructure.EventStore;
import io.plumery.eventstore.jdbc.JdbcEventStore;
import io.plumery.eventstore.local.LocalEventStore;
import liquibase.exception.LiquibaseException;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class EventStoreFactory {
    private static Logger LOG = LoggerFactory.getLogger(EventStoreFactory.class);
    private static final String LOCAL = "local";
    private static final String JDBC = "jdbc";

    private String bootstrap;

    @JsonProperty
    private String type = LOCAL;

    @JsonProperty
    private DataSourceFactory database = new DataSourceFactory();

    public void setBootstrap(String bootstrap) {
        this.bootstrap = bootstrap;
    }

    public String getBootstrap() {
        return bootstrap;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public EventStore build(Environment environment, EventPublisher publisher, String eventsPackage) {
        EventStore eventStore;

        if (type.equals(JDBC)) {
            eventStore = buildJdbcEventStore(environment);
        } else {
            eventStore = new LocalEventStore(publisher);
        }

        LOG.info("Configured EventStore ["+ eventStore.getClass().getSimpleName() +
                "] with Events Package [" + eventsPackage + "] and Publisher ["
                + publisher.getClass().getSimpleName() + "]");

        return eventStore;
    }

    private EventStore buildJdbcEventStore(Environment environment) {
        ManagedDataSource ds = database.build(environment.metrics(), "eventstore");
        DBI jdbi = new DBIFactory().build(environment, database, "eventstore");
        updateDatabaseSchema(ds);
        EventStore eventStore = new JdbcEventStore(jdbi, environment.getObjectMapper());
        return eventStore;
    }

    private void updateDatabaseSchema(ManagedDataSource ds) {
        try {
            try {
                CloseableLiquibase lq = new CloseableLiquibaseWithClassPathMigrationsFile(ds, "migrations.xml");
                lq.update("");
                lq.close();
            } finally {
                if (ds != null) {
                    ds.stop();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can not execute db migrations.", e);
        } catch (LiquibaseException e) {
            throw new RuntimeException("Can not execute db migrations.", e);
        } catch (Exception e) {
            throw new RuntimeException("Can not execute db migrations.", e);
        }
    }

    public DataSourceFactory getDataSourceFactory() {
        return database;
    }
}
