package io.plumery.messaging.camel;

import io.plumery.core.Command;
import io.plumery.core.infrastructure.CommandDispatcher;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;

public class CamelCommandDispatcher implements CommandDispatcher {
    public static final String ENDPOINT_DISPATCH_COMMAND = "direct:dispatchCommand";
    private final ProducerTemplate producer;

    public CamelCommandDispatcher(CamelContext camelContext) {
        try {
            this.producer = camelContext.createProducerTemplate();
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T extends Command> void dispatch(T command) {
        producer.sendBody(ENDPOINT_DISPATCH_COMMAND, command);
    }
}
