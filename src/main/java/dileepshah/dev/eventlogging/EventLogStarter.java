package dileepshah.dev.eventlogging;

import dileepshah.dev.eventlogging.consumer.EventLogConsumer;
import dileepshah.dev.eventlogging.io.BatchLogWriterImpl;
import dileepshah.dev.eventlogging.io.LogWriter;
import dileepshah.dev.eventlogging.model.LogRequest;
import dileepshah.dev.eventlogging.producer.EventLogProducer;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Main class for starting the event logging consumer and producer.
 */
public class EventLogStarter {
    public static void main(String[] args) {
        final BlockingDeque<LogRequest> logRequestBlockingDeque = new LinkedBlockingDeque<>(10000);
        final LogWriter logWriter = new BatchLogWriterImpl(
                EventLogStarter.class.getProtectionDomain().getCodeSource().getLocation().getPath(),
                "log.txt");
        final EventLogConsumer eventLogConsumer = EventLogConsumer.builder()
                .logRequestBlockingDeque(logRequestBlockingDeque)
                .logWriter(logWriter)
                .build();
        eventLogConsumer.start();

        // Have multiple producers producing the event logs at the same time and then see how they are
        // handled and persisted and what is the latency and throughput of the system.
        try(final ExecutorService executors = Executors.newFixedThreadPool(1000)) {
            for (int i = 0; i < 1000; i++) {
                executors.submit(EventLogProducer.builder().producerName("Producer " + i)
                        .logRequestBlockingDeque(logRequestBlockingDeque).build());
            }
        }

        eventLogConsumer.shutdown();
    }
}
