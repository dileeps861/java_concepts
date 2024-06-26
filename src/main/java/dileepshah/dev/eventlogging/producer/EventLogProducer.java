package dileepshah.dev.eventlogging.producer;

import dileepshah.dev.eventlogging.consumer.EventLogConsumer;
import dileepshah.dev.eventlogging.model.LogMessage;
import dileepshah.dev.eventlogging.model.LogRequest;
import lombok.Builder;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CountDownLatch;

/**
 * Produces event logs.
 * @see EventLogConsumer
 * @see EventLogConsumer#start()
 * @see dileepshah.dev.eventlogging.EventLogStarter
 */
@Builder
public class EventLogProducer implements Runnable{
    private final String producerName;
    private final BlockingDeque<LogRequest> logRequestBlockingDeque; // <LogRequest>
    @Builder.Default
    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            LogRequest logRequest = LogRequest.builder()
                    .logMessage(LogMessage.builder()
                            .timestamp(System.currentTimeMillis())
                            .message(producerName + " - " + i)
                            .build())
                    .latch(countDownLatch)
                    .build();
            long startTime = System.currentTimeMillis();
            logRequestBlockingDeque.add(logRequest);
            try {
                countDownLatch.await();

                long endTime = System.currentTimeMillis();
                if(endTime - startTime > 50) {
                    System.out.println("Latency for message: " + producerName + " - " + i + " is: " + (endTime - startTime));
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
