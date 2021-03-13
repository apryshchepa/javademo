package com.example.logparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

// initially I've planned to use JPA, that's why I've created Spring Boot project
// but then I've realized that I do not have enough time, and took a shortcut with JDBC
public class DatabaseWriter
    implements Runnable, IDatabaseWriter {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseWriter.class);

    private volatile boolean stop = false;
    private final LinkedBlockingQueue<List<LogEvent>> queue = new LinkedBlockingQueue<>();
    private final Connection connection;

    public DatabaseWriter() throws SQLException {
        connection = DriverManager.getConnection("jdbc:hsqldb:file:demodb", "sa", "");
        ensureTableExists();
    }

    public void run() {
        while(!stop || !queue.isEmpty()) {
            List<LogEvent> data = null;
            try {
                data = queue.take();
                LOG.debug("queue size: {}", queue.size());
            } catch (InterruptedException e) {
                LOG.error("Can't read parsed events form the queue", e);
            }

            write(data);
        }

        try {
            connection.close();
        } catch (SQLException e) {
            LOG.error("Can't close connection", e);
        }
    }

    @Override
    public void enqueueWrite(List<LogEvent> data) {
        queue.add(data);
    }

    @Override
    public void terminate() {
        this.stop = true;
        queue.add(new LinkedList<>());
    }

    @Override
    public int getQueueSize() {
        return queue.size();
    }

    private void write(List<LogEvent> data) {
        if (data == null) {
            LOG.error("'null' collection has been sent to database, what should never be the case.");
            return;
        }
        if (data.isEmpty()) {
            return;
        }

        LOG.debug("Writing {} rows into the database", data.size());
        try {
            insert(data);
        } catch (SQLException e) {
            LOG.error("Can't write to the database", e);
        }
        LOG.debug("Done writing {} rows into the database", data.size());
    }

    private void insert(List<LogEvent> data) throws SQLException {
        String sqlInsert = "INSERT INTO events(id, duration, type, host, alert) VALUES (?,?,?,?,?)";
        PreparedStatement eventStatement = connection.prepareStatement(sqlInsert);
        for (LogEvent event: data) {
            eventStatement.setString(1, event.getId());
            eventStatement.setLong(2, event.getDuration());
            eventStatement.setString(3, event.getType());
            eventStatement.setString(4, event.getHost());
            eventStatement.setBoolean(5, event.isAlert());
            eventStatement.addBatch();
        }
        eventStatement.executeBatch();
    }

    private void ensureTableExists() throws SQLException {
        String sqlCreate = "CREATE TABLE IF NOT EXISTS events"
                + "  (pk              INTEGER IDENTITY PRIMARY KEY,"
                + "   id              varchar(50),"
                + "   duration        bigint,"
                + "   type            varchar(50),"
                + "   host            varchar(50),"
                + "   alert           bit)";

        Statement stmt = this.connection.createStatement();
        stmt.execute(sqlCreate);
    }
}
