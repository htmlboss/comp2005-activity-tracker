package com.activitytracker;

import java.sql.*;

import java.io.File;

class DBManager {
    private Connection conn = null;

    DBManager() {
    }

    private boolean executeUpdate(final String sqlQuery) {
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sqlQuery);
            stmt.close();
        }
        catch(final SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }

        return true;
    }

    // Initialize connection to an sqlite database.
    // Returns true if successful, false otherwise.
    boolean init(final String dbURL) {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbURL);
        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
        System.out.println("Opened database successfully.");

        // Check if database file exists on disk.
        // If not, create tables in memory.
        final boolean dbFileExists = new File(dbURL).isFile();
        if (!dbFileExists) {
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
