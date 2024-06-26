package dileepshah.dev.eventlogging.consumer;

import dileepshah.dev.eventlogging.io.LogWriter;
import dileepshah.dev.eventlogging.model.LogRequest;
import lombok.Builder;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Event logging server, which logs events into a log file. The server keeps listening for log requests
 * and adds them to its blocking queue. The server should be started in a separate thread.
 * Also, flusher should be trying to flush the log file.
 */
@Builder
public class EventLogConsumer {
    private final BlockingDeque<LogRequest> logRequestBlockingDeque;
    private final LogWriter logWriter;
    @Builder.Default
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    public void start() {
        System.out.println("Starting Event Log Consumer");
        executorService.submit(() -> run());

    }

    public Runnable run() {
        while (true) {
            try {
                LogRequest logRequest = logRequestBlockingDeque.take();
                logWriter.write(logRequest);
                logWriter.flush();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void shutdown() {
        logWriter.close();
        executorService.shutdown();
        System.out.println("Shutting down Event Log Consumer");
    }
}
