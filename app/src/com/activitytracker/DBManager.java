package com.activitytracker;

import java.sql.*;

class DBManager {
    private Connection m_conn = null;

    DBManager() {
    }

    private ResultSet executeQuery(final String sqlQuery) {
        ResultSet res = null;

        try {
            Statement stmt = m_conn.createStatement();
            res = stmt.executeQuery(sqlQuery);
            stmt.close();
        }
        catch(final SQLException e) {
            System.err.println(e.getMessage());
        }

        return res;
    }

    private boolean executeUpdate(final String sqlQuery) {
        try {
            Statement stmt = m_conn.createStatement();
            stmt.executeUpdate(sqlQuery);
            stmt.close();
        }
        catch(final SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }

        return true;
    }

    // Counts the number of tables in the open database.
    // If the table count == 0 the database is considered
    // empty.
    private boolean isEmpty() {

        try {
            final DatabaseMetaData dbmd = m_conn.getMetaData();
            final String[] types = {"TABLE"};
            final ResultSet rs = dbmd.getTables(null, null, "%", types);

            return !rs.next();
        }
        catch(final SQLException e) {
            System.err.println(e.getMessage());
            return true;
        }

    }

    // Initialize connection to an sqlite database.
    // Returns true if successful, false otherwise.
    boolean init(final String dbURL) {
        try {
            m_conn = DriverManager.getConnection("jdbc:sqlite:" + dbURL);
        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
        System.out.println("Opened database successfully.");

        if (isEmpty()) {
            System.out.println("Creating tables...");

            // Create users table
            String sqlQuery = "CREATE TABLE USERS (" +
                    "    id            INTEGER PRIMARY KEY ASC AUTOINCREMENT NOT NULL," +
                    "    email_address STRING  NOT NULL UNIQUE ON CONFLICT FAIL," +
                    "    name          STRING  NOT NULL," +
                    "    date_of_birth DATE    NOT NULL," +
                    "    sex           CHAR    NOT NULL," +
                    "    height        REAL    NOT NULL," +
                    "    weight        REAL    NOT NULL" +
                    ");";
            if (!executeUpdate(sqlQuery)) {
                return false;
            }

            // Create passwords table
            sqlQuery = "CREATE TABLE PASSWORDS (" +
                    "    ID          INTEGER  PRIMARY KEY ASC AUTOINCREMENT NOT NULL," +
                    "    UserID      INTEGER  NOT NULL REFERENCES USERS (id)," +
                    "    PassHash    STRING   NOT NULL," +
                    "    DateChanged DATETIME NOT NULL" +
                    ");";

            if (!executeUpdate(sqlQuery)) {
                return false;
            }

            // Create workouts table
            sqlQuery = "CREATE TABLE WORKOUTS (" +
                    "    ID       INTEGER PRIMARY KEY ASC AUTOINCREMENT NOT NULL," +
                    "    UserID   INTEGER NOT NULL REFERENCES USERS (id)," +
                    "    WOType   STRING  NOT NULL," +
                    "    Date     DATE    NOT NULL," +
                    "    Duration TIME    NOT NULL," +
                    "    kCal     REAL    NOT NULL" +
                    ");";

            if (!executeUpdate(sqlQuery)) {
                return false;
            }

            // Create friends table
            sqlQuery = "CREATE TABLE FRIENDS (" +
                    "    ID          INTEGER  PRIMARY KEY ASC AUTOINCREMENT NOT NULL," +
                    "    Sender      INTEGER  NOT NULL REFERENCES USERS (id)," +
                    "    Receiver    INTEGER  REFERENCES USERS (id)," +
                    "    SendDate    DATETIME NOT NULL," +
                    "    ConfirmDate DATETIME DEFAULT NULL" +
                    ");";

            if (!executeUpdate(sqlQuery)) {
                return false;
            }

        }

        return true;
    }

}
