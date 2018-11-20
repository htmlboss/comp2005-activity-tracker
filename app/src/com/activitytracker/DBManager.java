package com.activitytracker;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Calendar;

class DBManager {
    private Connection m_conn = null;

    DBManager() {
    }

    // Adds rows to Users table and Passwords table with the new user's attributes
    // If the user exists in the database, raises an exception
    public void createUser(final String name, final String emailAddress, final int DOBYear, final int DOBMonth, final int DOBDay,
                           final User.Sex sex, final float height, final float weight, final SecureString securePassword) {

        if (!userExists(emailAddress)) {
            String sqlQuery = "INSERT INTO Users (" +
                    "email_address, " +
                    "name, " +
                    "date_of_birth, " +
                    "sex, " +
                    "height, " +
                    "weight," +
                    "password_hash," +
                    "created_at" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            byte sexByte = sex.equals(User.Sex.MALE) ? (byte) 1 : (byte) 0;
            java.sql.Date currentTime = new java.sql.Date(System.currentTimeMillis());
            Calendar c = Calendar.getInstance();
            c.set(DOBYear, DOBMonth, DOBDay);
            java.sql.Date dateOfBirth = new java.sql.Date(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

            try {
                PreparedStatement stmt = m_conn.prepareStatement(sqlQuery);
                stmt.setString(1, emailAddress);
                stmt.setString(2, name);
                stmt.setDate(3, dateOfBirth);
                stmt.setByte(4, sexByte);
                stmt.setFloat(5, height);
                stmt.setFloat(6, weight);
                stmt.setString(7, securePassword.toString());
                stmt.setDate(8, currentTime);

                if (stmt.executeUpdate() != 1) {
                    System.err.println("User not added to database.");
                }

                stmt.close();

            }
            catch (final SQLException e) {
                System.err.println(e.getMessage());
            }


        }
        else {
            throw new AssertionError("User with email address '" + emailAddress + "' already exists.");
        }

    }

    public  boolean userExists(final String emailAddress) {
        String sqlQuery = "SELECT COUNT(*) AS count FROM Users WHERE `email_address`=?";
        boolean exists = false;

        try {
            PreparedStatement stmt = m_conn.prepareStatement(sqlQuery);
            stmt.setString(1, emailAddress);
            ResultSet res = stmt.executeQuery();
            exists = res.getInt("count") > 0;
            stmt.close();
        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
        }

        return exists;

    }

    // Overloaded version of below data extraction method
    // Necessary because we will get things corresponding to an ID but the user
    // logs in with an email address, not their ID initially
    public Object getUserAttribute(final UserAttribute attribute, final String emailAddress) {
        int id;
        ResultSet res;
        try {
            PreparedStatement stmt = m_conn.prepareStatement("SELECT id FROM Users WHERE `email_address`=?");
            stmt.setString(1, emailAddress);
            res =  stmt.executeQuery();
            stmt.close();
        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }

        if (res != null) {
            return null;
        }

        try {
            id = res.getInt("id");
        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }

        if (attribute == UserAttribute.ID) {
            return id;
        }
        else {
            return getUserAttribute(attribute, id);
        }

    }

    // Return a user's attribute given the user ID
    public Object getUserAttribute(final UserAttribute attribute, final int id) {
        ResultSet res;
        PreparedStatement stmt;
        String sqlQuery;

        switch (attribute) {
            case ID:
                return id;
            case NAME:
                sqlQuery = "SELECT name FROM Users WHERE id=?";
                break;
            case SEX:
                sqlQuery = "SELECT sex FROM Users WHERE id=?";
                break;
            case HEIGHT:
                sqlQuery = "SELECT height FROM Users WHERE id=?";
                break;
            case WEIGHT:
                sqlQuery = "SELECT weight FROM Users WHERE id=?";
                break;
            case DATE_OF_BIRTH:
                sqlQuery = "SELECT date_of_birth FROM Users WHERE id=?";
                break;
            case EMAIL_ADDRESS:
                sqlQuery = "SELECT email_address FROM Users WHERE id=?";
                break;
            default:
                return null;
        }

        try {
            stmt = m_conn.prepareStatement(sqlQuery);
            stmt.setInt(1, id);
            res = stmt.executeQuery();
            stmt.close();

            switch (attribute) {
                case NAME:
                    return res.getString("name");
                case SEX:
                    if (res.getByte("sex") == (byte) 1)
                        return User.Sex.MALE;
                    else
                        return User.Sex.FEMALE;
                case HEIGHT:
                    return res.getFloat("height");
                case WEIGHT:
                    return res.getFloat("weight");
                case DATE_OF_BIRTH:
                    return res.getDate("date_of_birth");
                case EMAIL_ADDRESS:
                    return res.getString("email_address");
                default:
                    return null;
            }

        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }

    }

    // Updates a user's row in the DB
    // Uses UserAttribute enum to specify column in the DB; pass in Object since different cols are different
    // types. I know this contradicts Java being strongly typed but it will have to work for now.
    public boolean setUserAttribute(final UserAttribute attribute, final int id, final Object changeTo) {
        PreparedStatement stmt;
        String sqlQuery;
        try {
            switch (attribute) {
                case ID:
                    System.err.println("Action prohibited: set User.id");
                    return false;
                case NAME:
                    sqlQuery = "UPDATE Users SET name=? WHERE id=?";
                    stmt = m_conn.prepareStatement(sqlQuery);
                    stmt.setString(1, (String) changeTo);
                    stmt.setInt(2, id);
                    break;
                case SEX:
                    byte sexByte = (byte) (((User.Sex) changeTo).equals(User.Sex.MALE) ? 1 : 0);
                    sqlQuery = "UPDATE Users SET sex=? WHERE id=?";
                    stmt = m_conn.prepareStatement(sqlQuery);
                    stmt.setByte(1, sexByte);
                    stmt.setInt(2, id);
                    break;
                case HEIGHT:
                    sqlQuery = "UPDATE Users SET height=? WHERE id=?";
                    stmt = m_conn.prepareStatement(sqlQuery);
                    stmt.setFloat(1, (float) changeTo);
                    stmt.setInt(2, id);
                    break;
                case WEIGHT:
                    sqlQuery = "UPDATE Users SET weight=? WHERE id=?";
                    stmt = m_conn.prepareStatement(sqlQuery);
                    stmt.setFloat(1, (float) changeTo);
                    stmt.setInt(2, id);
                    break;
                case DATE_OF_BIRTH:
                    sqlQuery = "UPDATE Users SET date_of_birth=? WHERE id=?";
                    stmt = m_conn.prepareStatement(sqlQuery);
                    stmt.setDate(1, (java.sql.Date) changeTo);
                    stmt.setInt(2, id);
                    break;
                case EMAIL_ADDRESS:
                    sqlQuery = "UPDATE Users SET email_address=? WHERE id=?";
                    stmt = m_conn.prepareStatement(sqlQuery);
                    stmt.setString(1, (String) changeTo);
                    stmt.setInt(2, id);
                    break;
                default:
                    return false;
            }

            stmt.executeUpdate();
            stmt.close();

        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }

        return true;

    }

    // Can ONLY be used when passing strings that DO NOT contain user input
    // NOT protected from SQL injection; use a PreparedStatement.setXXX() instead
    private ResultSet executeQuery(final String sqlQuery) {
        ResultSet res = null;

        try {
            Statement stmt = m_conn.createStatement();
            res = stmt.executeQuery(sqlQuery);
            stmt.close();
        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
        }

        return res;
    }

    // Can ONLY be used when passing strings that DO NOT contain user input
    // NOT protected from SQL injection; use a PreparedStatement.setXXX() instead
    private boolean executeUpdate(final String sqlQuery) {
        try {
            Statement stmt = m_conn.createStatement();
            stmt.executeUpdate(sqlQuery);
            stmt.close();
        }
        catch (final SQLException e) {
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
        catch (final SQLException e) {
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
                    // 1 corresponds to male; 0 to female
                    "    sex           BIT(1)  NOT NULL," +
                    "    height        REAL    NOT NULL," +
                    "    weight        REAL    NOT NULL," +
                    "    password_hash STRING  NOT NULL," +
                    "    created_at    DATE    NOT NULL" +
                    ")";
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
                    ")";

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
                    ")";

            if (!executeUpdate(sqlQuery)) {
                return false;
            }

        }

        return true;
    }

}
