package io.plumery.inventoryitem.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import io.plumery.core.ID;
import io.plumery.core.infrastructure.CommandDispatcher;
import io.plumery.core.serializer.IDSerializer;
import io.plumery.inventoryitem.api.denormalizer.KafkaDenormalizer;
import io.plumery.inventoryitem.api.denormalizer.hazelcast.HazelcastManaged;
import io.plumery.inventoryitem.api.query.InventoryItemsQuery;
import io.plumery.inventoryitem.api.resources.InventoryItemResource;
import io.plumery.inventoryitem.api.stream.StreamBroadcaster;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.media.sse.SseFeature;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

import static org.eclipse.jetty.servlets.CrossOriginFilter.*;

public class InventoryItemApi extends Application<InventoryItemApiConfiguration> {
    public static void main(String[] args) throws Exception {
        new InventoryItemApi().run(args);
    }

    @Override
    public void run(InventoryItemApiConfiguration configuration, Environment environment) throws Exception {
        configureObjectMapper(environment);

        CommandDispatcher commandDispatcher = configuration.getCommandDispatcherFactory().build(environment);

        environment.jersey().register(new ApiListingResource());
        environment.jersey().register(SseFeature.class);

        InventoryItemResource resource = new InventoryItemResource(new InventoryItemsQuery(), commandDispatcher);
        environment.jersey().register(resource);
        environment.lifecycle().manage(new KafkaDenormalizer(configuration));
        environment.lifecycle().manage(new HazelcastManaged());

        StreamBroadcaster broadcaster = configuration.getStreamBroadcasterFactory().build(environment);
        broadcaster.addObserver(resource);

        configureSwagger(environment);
    }

    private void configureSwagger(Environment environment) {
        BeanConfig config = new BeanConfig();
        config.setTitle("Inventory Item API");
        config.setVersion("1.0.0");
        config.setResourcePackage(InventoryItemResource.class.getPackage().getName());
        config.setScan(true);

        FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORSFilter", CrossOriginFilter.class);
        filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, environment.getApplicationContext().getContextPath() + "swagger.json");
        filter.setInitParameter(ALLOWED_METHODS_PARAM, "GET,OPTIONS");
        filter.setInitParameter(ALLOWED_HEADERS_PARAM, "Origin, Content-Type, Accept");
        filter.setInitParameter(ALLOWED_ORIGINS_PARAM, "*");
        filter.setInitParameter(ALLOW_CREDENTIALS_PARAM, "true");
    }

    private static void configureObjectMapper(Environment environment) {
        ObjectMapper mapper = environment.getObjectMapper();
        mapper.findAndRegisterModules();

        SimpleModule module = new SimpleModule();
        module.addSerializer(ID.class, new IDSerializer());
        mapper.registerModule(module);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
