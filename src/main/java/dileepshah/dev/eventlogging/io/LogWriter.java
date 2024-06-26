package dileepshah.dev.eventlogging.io;

import dileepshah.dev.eventlogging.model.LogRequest;

/**
 * Interface for a log writer, which writes log request into a log file
 */
public interface LogWriter {
    void write(LogRequest logRequest);
    void flush();

    void close();
}
