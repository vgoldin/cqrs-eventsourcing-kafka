package io.plumery.intentoryitem.api.test;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import io.plumery.inventoryitem.api.denormalizer.KafkaDenormalizer;

/**
 * Created by veniamin on 19/02/2017.
 */
public class TestDenormalizer {
    public static void main(String[] args) {
        HazelcastInstance instance = Hazelcast.newHazelcastInstance();

        KafkaDenormalizer denormalizer = new KafkaDenormalizer();
        try {
            denormalizer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
