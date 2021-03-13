package com.example.logparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class LogParser {

    //parsing in multiple threads doesn't make much sense, as database is a bottleneck
    private static final int CHUNK_SIZE = 500;
    private static final Logger LOG = LoggerFactory.getLogger(LogParser.class);

    private final IParsingStrategy strategy;
    private final IDatabaseWriter databaseWriter;
    private final Map<String, LogEntry> dictionary;
    private List<LogEvent> chunk;

    public LogParser(IParsingStrategy strategy, IDatabaseWriter databaseWriter) {
        this.strategy = strategy;
        this.databaseWriter = databaseWriter;
        this.dictionary = new HashMap<>();
        this.chunk = new LinkedList<>();
    }

    public void parse(String path) throws IOException {
        parseFile(path);
        verifyLeftovers();
    }

    private void parseFile(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();
            while (line != null) {
                LogEntry logEntry = strategy.Parse(line);
                if (logEntry != null) {
                    if (dictionary.containsKey(logEntry.getId())) {
                        LogEntry previousLogEntry = dictionary.get(logEntry.getId());
                        dictionary.remove(logEntry.getId());
                        LogEvent event = new LogEvent(logEntry, previousLogEntry);
                        save(event);
                    }
                    else {
                        dictionary.put(logEntry.getId(), logEntry);
                    }
                }
                line = reader.readLine();
            }
            flushLastChunk();
            databaseWriter.terminate();
        }
    }

    private void flushLastChunk() {
        databaseWriter.enqueueWrite(chunk);
    }

    private void save(LogEvent event) {
        chunk.add(event);
        if (chunk.size() >= CHUNK_SIZE) {
            databaseWriter.enqueueWrite(chunk);
            chunk = new LinkedList<>();
        }
    }

    private void verifyLeftovers() {
        if (dictionary.isEmpty()) {
            return;
        }

        String orphanRecords = String.join(", ", dictionary.keySet());
        LOG.warn("There are orphan log entries: {}", orphanRecords);
    }
}
