import com.fasterxml.jackson.databind.ObjectMapper;
import io.plumery.core.Event;
import io.plumery.eventstore.kafka.KafkaEventStore;
import io.plumery.inventoryitem.core.domain.InventoryItem;
import io.plumery.inventoryitem.core.events.InventoryItemCreated;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by veniamin on 13/02/2017.
 */
public class TestReloadEventsToAggregateRoot {
    @Test
    @Ignore
    public void testReloadEventsToAggregateRoot() {
        KafkaEventStore eventStore =
                new KafkaEventStore.Builder()
                    .withZookeeper("localhost:9092")
                    .withGroupId("test")
                    .withObjectMapper(new ObjectMapper())
                    .withEventsPackage(InventoryItemCreated.class.getPackage().getName())
                    .build();

        Iterable<? extends Event> events  = eventStore.getEventsForAggregate(InventoryItem.class, "9854ccc1-d832-42b1-85ae-621309068310");

    }
}
