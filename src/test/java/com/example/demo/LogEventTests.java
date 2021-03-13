package com.example.demo;

import com.example.logparser.LogEntry;
import com.example.logparser.LogEvent;
import com.example.logparser.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LogEventTests {

    private LogEntry entry1;
    private LogEntry entry2;

    @BeforeEach
    public void init() {
        this.entry1 = new LogEntry()
        {{
            setId("1");
            setState(State.STARTED);
            setHost("host1");
            setType("type1");
            setTimestamp(1);
        }};
        this.entry2 = new LogEntry()
        {{
            setId("2");
            setState(State.FINISHED);
            setHost("host2");
            setType("type2");
            setTimestamp(3);
        }};
    }

    @Test
    public void logEventInitialization() {
        LogEvent instance = new LogEvent(this.entry1, this.entry2);
        assertThat(instance.getId()).isEqualTo(this.entry1.getId());
        assertThat(instance.getHost()).isEqualTo(this.entry1.getHost());
        assertThat(instance.getType()).isEqualTo(this.entry1.getType());
        assertThat(instance.getDuration()).isEqualTo(this.entry2.getTimestamp() - this.entry1.getTimestamp());
        assertThat(instance.isAlert()).isFalse();
    }

    @Test
    public void logEventAlert() {
        this.entry2.setTimestamp(6);

        LogEvent instance = new LogEvent(this.entry1, this.entry2);
        assertThat(instance.isAlert()).isTrue();
    }
}
