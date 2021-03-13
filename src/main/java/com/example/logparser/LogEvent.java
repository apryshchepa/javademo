package com.example.logparser;

public class LogEvent {
    private final String id;
    private final String type;
    private final String host;
    private final long duration;

    public LogEvent(LogEntry entry1, LogEntry entry2)
    {
        this.id = entry1.getId();
        this.type = entry1.getType(); // intentionally not handling possible inconsistency of "type" and "host" values and start being before finish
        this.host = entry1.getHost();
        this.duration = Math.abs(entry1.getTimestamp() - entry2.getTimestamp());
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getHost() {
        return host;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isAlert() {
        return duration > 4;
    }
}
