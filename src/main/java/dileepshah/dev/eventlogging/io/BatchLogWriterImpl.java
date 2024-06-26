package dileepshah.dev.eventlogging.io;

import dileepshah.dev.eventlogging.model.LogRequest;
import lombok.SneakyThrows;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class BatchLogWriterImpl implements LogWriter {
    private static final int MAX_BATCH_SIZE = 100;
    private final BufferedWriter bufferedWriter;
    private final List<LogRequest> logRequestsBuffer;

    @SneakyThrows
    public BatchLogWriterImpl(String filePath, String fileName) {
        this.logRequestsBuffer = new LinkedList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        System.out.println("Log file path: " + filePath + File.separator + fileName);
        file = new File(filePath + File.separator + fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create log file for file path: " + filePath);
            }
        }
        this.bufferedWriter = new BufferedWriter(new FileWriter(filePath + File.separator + fileName));
    }


    @Override
    @SneakyThrows
    public void write(LogRequest logRequest) {
        logRequestsBuffer.add(logRequest);
        if (logRequestsBuffer.size() >= MAX_BATCH_SIZE) {
            flush();
        }
    }

    @Override
    @SneakyThrows
    public void flush() {
        if (logRequestsBuffer.isEmpty()) {
            return;
        }

        flushLogMessage();
        notifyClients();
        logRequestsBuffer.clear();

    }

    @Override
    public void close() {
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void notifyClients() {
        logRequestsBuffer.forEach(logRequest -> logRequest.getLatch().countDown());
    }

    private void flushLogMessage() {
        logRequestsBuffer.forEach(logRequest -> {
            try {
                bufferedWriter.write(logRequest.getLogMessage().toString());
                bufferedWriter.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        try {
            bufferedWriter.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
