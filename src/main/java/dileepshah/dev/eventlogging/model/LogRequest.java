package dileepshah.dev.eventlogging.model;

import lombok.Builder;
import lombok.Getter;

import java.util.concurrent.CountDownLatch;

@Getter
@Builder
public class LogRequest {
    private final LogMessage logMessage;
    private final CountDownLatch latch;
}
