package com.example.logparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public final class LogSampleGenerator {

    private LogSampleGenerator() {}

    private static final Logger LOG = LoggerFactory.getLogger(LogSampleGenerator.class);
    private static final String LOG_FORMAT = "{\"id\":\"%d\", \"state\":\"%s\", \"timestamp\":\"%d\"}\n";

    public static void generate(String path, int eventsCount) throws IOException {
        LOG.info("Generating file with {} events", eventsCount);

        File file = new File(path);
        //noinspection ResultOfMethodCallIgnored
        file.delete();

        Random r = new Random();
        try (FileWriter logWriter = new FileWriter(path)) {
            for (int i = 0; i < eventsCount; i++) {
                int random = r.nextInt(500);
                long start = System.currentTimeMillis() - random;

                logWriter.write(String.format(LOG_FORMAT,
                        i,
                        State.STARTED.toString(),
                        start));

                random = r.nextInt(20);

                logWriter.write(String.format(LOG_FORMAT,
                        i,
                        State.FINISHED,
                        start + random));
            }
        }
    }
}
