package dileepshah.dev.eventlogging.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class LogMessage {
    private final long timestamp;
    private final String message;
}
