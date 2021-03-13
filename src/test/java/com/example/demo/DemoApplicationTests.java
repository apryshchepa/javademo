package com.example.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class DemoApplicationTests {

    private DemoApplication instance = new DemoApplication();

    @ParameterizedTest
    @ValueSource(strings = {"", "not_existing_path"})
    void badPath(String args) {
        instance.run(args);
        assertThat(instance.getExitCode()).isEqualTo(DemoApplication.EXIT_CODE_FILE_ACCESS_ERROR);
    }

    @Test
    void emptyArgs() {
        instance.run();
        assertThat(instance.getExitCode()).isEqualTo(DemoApplication.EXIT_CODE_BAD_ARGS);
    }

    @Test
    void wrongEventsCountArg() {
        instance.run("path", "-g", "notInteger");
        assertThat(instance.getExitCode()).isEqualTo(DemoApplication.EXIT_CODE_BAD_ARGS);
    }

}
