package com.activitytracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class DBManager {
    private Connection connection = null;
    private long queryTimeout = 10; // 10 seconds for query timeout

    DBManager() {
    }

    // Initialize connection to an sqlite database.
    // Returns true if successful, false otherwise.
    boolean init(final String dbURL) {
        try {
            connection = DriverManager.getConnection(dbURL);
        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }

        return true;
    }

}
