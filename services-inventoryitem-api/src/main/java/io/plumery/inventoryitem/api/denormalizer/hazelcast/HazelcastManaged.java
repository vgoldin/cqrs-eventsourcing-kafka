package io.plumery.inventoryitem.api.denormalizer.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import io.dropwizard.lifecycle.Managed;

public class HazelcastManaged implements Managed {
    private static HazelcastInstance hazelcastInstance;

    public static HazelcastInstance getInstance() {
        if (hazelcastInstance == null) {
            Config config = new XmlConfigBuilder().build();
            config.setInstanceName(Thread.currentThread().getName());

            hazelcastInstance = Hazelcast.getOrCreateHazelcastInstance(config);
        }

        return hazelcastInstance;
    }

    @Override
    public void start() throws Exception {
    }

    @Override
    public void stop() throws Exception {
        Hazelcast.shutdownAll();
    }
}
