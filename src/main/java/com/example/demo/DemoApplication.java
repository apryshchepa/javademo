package com.example.demo;

import com.example.logparser.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.sql.SQLException;

@SpringBootApplication
public class DemoApplication
        implements CommandLineRunner, ExitCodeGenerator {

    private static final int EXIT_CODE_OK = 0;
    // exit codes between 1 and 127 are used by environment
    public static final int EXIT_CODE_UNKNOWN = 128;
    public static final int EXIT_CODE_BAD_ARGS = 129;
    public static final int EXIT_CODE_FILE_ACCESS_ERROR = 130;

    private static final Logger LOG = LoggerFactory.getLogger(DemoApplication.class);

    private String lineParsingStrategy;
    private String path;
    private int eventsCount;
    private int exitCode;
    private Thread databaseWriterThread;

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(DemoApplication.class, args)));
    }

    @Override
    public void run(String... args) {
        LOG.debug("Started from: {}", System.getProperty("user.dir"));
        LOG.info("work started");
        try {
            this.exitCode = processArguments(args);
        } catch (IOException e) {
            LOG.error("Problem with file access", e);
            this.exitCode = EXIT_CODE_FILE_ACCESS_ERROR;
        } catch (SQLException e) {
            LOG.error("Problem with database access", e);
            this.exitCode = EXIT_CODE_FILE_ACCESS_ERROR;
        } catch (InterruptedException e) {
            LOG.error("Unexpected exception", e);
            this.exitCode = EXIT_CODE_UNKNOWN;
        }
        LOG.info("work finished");
    }

    @Override
    public int getExitCode() {
        return this.exitCode;
    }

    private int processArguments(String... args) throws IOException, SQLException, InterruptedException {
        this.lineParsingStrategy = "-r";
        this.eventsCount = 10;

        Mode mode = parseInput(args);
        switch (mode) {
            case PRINT_HELP:
                printUsage();
                return EXIT_CODE_BAD_ARGS;
            case GENERATE:
                LogSampleGenerator.generate(path, eventsCount);
                break;
            case PARSE:
                IParsingStrategy parsingStrategy = ParsingStrategyFactory.getInstance(lineParsingStrategy);
                IDatabaseWriter writer = allocateDatabaseWriter();
                LogParser parser = new LogParser(parsingStrategy, writer);
                parser.parse(path);
                databaseWriterThread.join();
                break;
        }

        return EXIT_CODE_OK;
    }

    private Mode parseInput(String... args) {
        if (args.length < 1) {
            return Mode.PRINT_HELP;
        }

        this.path = args[0];

        if (args.length > 1) {
            switch (args[1]) {
                case "-r":
                case "-q":
                    this.lineParsingStrategy = args[1];
                    break;
                case "-g":
                    return tryRetrieveNumberOfEventsFromArgs(args) ? Mode.GENERATE : Mode.PRINT_HELP;
            }
        }

        return Mode.PARSE;
    }

    private boolean tryRetrieveNumberOfEventsFromArgs(String... args) {
        if (args.length <= 2) {
            return true;
        }

        try {
            this.eventsCount = Integer.parseInt(args[2]);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    private IDatabaseWriter allocateDatabaseWriter() throws SQLException {
        DatabaseWriter result = new DatabaseWriter();
        this.databaseWriterThread = new Thread(result);
        this.databaseWriterThread.start();
        return result;
    }

    private void printUsage() {
        System.out.println("Wrong input has been detected.");
        System.out.println("Usage: demo <file path> -parameter parameterValue");
        System.out.println("Parameters:\n");
        System.out.println("\t-q quick parsing strategy, no parameter value;");
        System.out.println("\t-r reliable parsing strategy, no parameter value;");
        System.out.println("\t(  -q and -r are mutually exclusive)");
        System.out.println("\t-g <number of events> example file generation mode;");
        System.out.println("\tSpare parameters or values are ignored.");
    }
}
