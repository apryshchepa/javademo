package com.example.logparser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReliableParsingStrategy
    implements IParsingStrategy{

    private static final Logger LOG = LoggerFactory.getLogger(ReliableParsingStrategy.class);
    private final ObjectMapper jsonSerializer;

    public ReliableParsingStrategy() {
        jsonSerializer = new ObjectMapper();
    }

    @Override
    public LogEntry Parse(String logEntry) {
        LogEntry result;
        try {
            result = jsonSerializer.readValue(logEntry, LogEntry.class);
        } catch (JsonProcessingException e) {
            LOG.error(String.format("Error parsing following entry: %s", logEntry), e);
            return null;
        }
        return result;
    }
}
