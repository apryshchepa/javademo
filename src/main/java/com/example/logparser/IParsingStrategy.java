package com.example.logparser;

public interface IParsingStrategy {
    LogEntry Parse(String logEntry);
}
