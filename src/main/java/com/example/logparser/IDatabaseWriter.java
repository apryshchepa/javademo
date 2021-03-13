package com.example.logparser;

import java.util.List;

public interface IDatabaseWriter {
    void enqueueWrite(List<LogEvent> data);

    void terminate();

    int getQueueSize();
}
