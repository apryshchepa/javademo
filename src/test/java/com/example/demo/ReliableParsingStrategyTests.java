package com.example.demo;

import com.example.logparser.LogEntry;
import com.example.logparser.ReliableParsingStrategy;
import com.example.logparser.State;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class ReliableParsingStrategyTests {

    private final ReliableParsingStrategy instance = new ReliableParsingStrategy();

    @Test
    void reliableParsingStrategyShortStringTest() {
        String [] input = {
                "{\"id\":\"identifier\", \"state\":\"STARTED\", \"timestamp\":\"1615634009092\"}",
                "{\"state\":\"STARTED\",\"id\":\"identifier\",\"timestamp\":\"1615634009092\"}",
                "{\"id\":\"identifier\", \"state\":\"STARTED\", \"type\":\"APP_LOG\", \"host\":\"12345\", \"timestamp\":\"1615634009092\"}",
                "{\"state\":\"STARTED\", \"id\":\"identifier\",  \"type\":\"APP_LOG\", \"timestamp\":\"1615634009092\", \"host\":\"12345\"}"};
        LogEntry[] output = Arrays.stream(input).map(instance::Parse).toArray(LogEntry[]::new);
        Arrays.stream(output).forEach(x -> {
            assertThat(x.getId()).isEqualTo("identifier");
            assertThat(x.getState()).isEqualTo(State.STARTED);
            assertThat(x.getTimestamp()).isEqualTo(1615634009092L);
        });
        assertThat(output[2].getHost()).isEqualTo("12345");
        assertThat(output[2].getType()).isEqualTo("APP_LOG");
        assertThat(output[3].getHost()).isEqualTo("12345");
        assertThat(output[3].getType()).isEqualTo("APP_LOG");
    }
}
