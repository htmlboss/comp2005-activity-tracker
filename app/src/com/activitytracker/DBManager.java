package com.activitytracker;

import javax.xml.transform.Result;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Calendar;

/**
 * Singleton class for the database. All classes and methods that interact with the database will use a
 * method in this class.
 *
 * Many times we are faced with the "chicken and egg" problem where we wish to create an object that is
 * populated with information from the database. So the question one faces is, "does the object's constructor
 * query the database (through the DBManager class, of course) for each attribute of the object that it
 * wishes to retrieve, or do we directly interact with a DBManager method which will then return a User or
 * Workout object, for example. We have decided to use the former methodology, with DBManager methods being as
 * general as possible, and often accepting enum types which then are put into a switch to create the
 * specific SQL query we wish to execute. This works best when all data returned is of the same data type
 * (for example, the Workout class will have three float attributes at the time of writing so we use one method
 * with return type of float for returning Workout attributes). This does not work as well when the object
 * requires data of multiple types --- for example, the User class. In this case, we have split the DBManager
 * methods into a single method for each attribute being returned.
 *
 * Polymorphism could theoretically be used here to simply have a return type of Object, however this is not
 * flexible and requires casting ALL returned data to the correct type in the invoking method.
 */
class DBManager {
    private Connection m_conn = null;

    /**
     * Test doc
     */
    DBManager() {
    }

    /**
     * Adds a row for a user to the Users table in the SQLite database for the app.
     *
     * Requires that the database tables exist and are in the correct format.
     * If the user exists in the database, raises an exception.
     *
     * @param name - User's name
     * @param emailAddress - User's email address; used to authenticate
     * @param DOBYear - The year the user was born
     * @param DOBMonth - The month the user was born
     * @param DOBDay - The day of month the user was born
     * @param sex - User.Sex.MALE or User.Sex.FEMALE
     * @param height - Floating point number of the user's height in metres
     * @param weight - Floating point number of the user's weight in kilograms
     * @param securePassword - A SecureString object containing the user's password, encrypted
     */
    public void createUser(final String name, final String emailAddress, final int DOBYear,
                           final int DOBMonth, final int DOBDay, final User.Sex sex, final float height,
                           final float weight, final SecureString securePassword) {

        if (!userExists(emailAddress)) {
            String sqlQuery = "INSERT INTO Users (" +
                    "email_address, " +
                    "name, " +
                    "date_of_birth, " +
                    "sex, " +
                    "height, " +
                    "weight," +
                    "password_hash," +
                    "password_salt," +
                    "created_at" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            byte sexByte = sex.equals(User.Sex.MALE) ? (byte) 1 : (byte) 0;
            java.sql.Date currentTime = new java.sql.Date(System.currentTimeMillis());
            Calendar c = Calendar.getInstance();
            c.set(DOBYear, DOBMonth, DOBDay);
            java.sql.Date dateOfBirth = new java.sql.Date(
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            );

            try {
                PreparedStatement stmt = m_conn.prepareStatement(sqlQuery);
                stmt.setString(1, emailAddress);
                stmt.setString(2, name);
                stmt.setDate(3, dateOfBirth);
                stmt.setByte(4, sexByte);
                stmt.setFloat(5, height);
                stmt.setFloat(6, weight);
                stmt.setString(7, securePassword.toString());
                stmt.setBytes(8, securePassword.getSalt());
                stmt.setDate(9, currentTime);

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

    /**
     *
     *
     * @param emailAddress - The user's email address for which we are checking existence.
     *                     We use email address here because this is what the user uses to log in to the app.
     * @return True if the user exists in the database, false otherwise.
     */
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

    public int getUserIDByEmail(final String emailAddress) {
        int id = 0;
        ResultSet res;
        String sqlQuery = "SELECT id FROM Users WHERE `email_address`=?";
        try {
            PreparedStatement stmt = m_conn.prepareStatement(sqlQuery);
            stmt.setString(1, emailAddress);
            res =  stmt.executeQuery();
            id = res.getInt("id");

            stmt.close();
        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
        }

        return id;
    }

    public String getUserPassHash(final int id) {
        String passHash;
        ResultSet res;
        String sqlQuery = "SELECT password_hash FROM Users WHERE id=?";
        try {
            PreparedStatement stmt = m_conn.prepareStatement(sqlQuery);
            stmt.setInt(1, id);
            res = stmt.executeQuery();
            passHash = res.getString("password_hash");

            stmt.close();
        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }

        return passHash;
    }

    public byte[] getUserPassSalt(final int id) {
        byte[] passSalt;
        ResultSet res;
        String sqlQuery = "SELECT password_salt FROM Users WHERE id=?";
        try {
            PreparedStatement stmt = m_conn.prepareStatement(sqlQuery);
            stmt.setInt(1, id);
            res = stmt.executeQuery();
            passSalt = res.getBytes("password_salt");
            stmt.close();
        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
        return passSalt;
    }

    public String getUserName(final int id) {
        String name;
        ResultSet res;
        String sqlQuery = "SELECT name FROM Users WHERE id=?";
        try {
            PreparedStatement stmt = m_conn.prepareStatement(sqlQuery);
            stmt.setInt(1, id);
            res = stmt.executeQuery();
            name = res.getString("name");

            stmt.close();
        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }

        return name;
    }

    public String getEmailAddress(final int id) {
        String email_address;
        ResultSet res;
        String sqlQuery = "SELECT email_address FROM Users WHERE id=?";
        try {
            PreparedStatement stmt = m_conn.prepareStatement(sqlQuery);
            stmt.setInt(1, id);
            res = stmt.executeQuery();
            email_address = res.getString("email_address");

            stmt.close();
        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }

        return email_address;
    }

    public Date getDateOfBirth(final int id) {
        Date DOB;
        java.sql.Date DOBResult;
        ResultSet res;
        String sqlQuery = "SELECT date_of_birth FROM Users WHERE id=?";
        try {
            PreparedStatement stmt = m_conn.prepareStatement(sqlQuery);
            stmt.setInt(1, id);
            res = stmt.executeQuery();
            DOBResult = res.getDate("date_of_birth");

            stmt.close();
        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
        DOB = new Date(DOBResult.getYear(), DOBResult.getMonth(), DOBResult.getDay());

        return DOB;
    }

    public User.Sex getUserSex(final int id) {
        byte sex;
        ResultSet res;
        String sqlQuery = "SELECT sex FROM Users WHERE id=?";
        try {
            PreparedStatement stmt = m_conn.prepareStatement(sqlQuery);
            stmt.setInt(1, id);
            res = stmt.executeQuery();
            sex = res.getByte("sex");

            stmt.close();
        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }

        if (sex == (byte) 1)
            return User.Sex.MALE;
        else
            return User.Sex.FEMALE;
    }

    public float getUserHeight(final int id) {
        float height;
        ResultSet res;
        String sqlQuery = "SELECT height FROM Users WHERE id=?";
        try {
            PreparedStatement stmt = m_conn.prepareStatement(sqlQuery);
            stmt.setInt(1, id);
            res = stmt.executeQuery();
            height = res.getFloat("height");

            stmt.close();
        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
            return 0.0f;
        }

        return height;
    }

    public float getUserWeight(final int id) {
        float weight;
        ResultSet res;
        String sqlQuery = "SELECT weight FROM Users WHERE id=?";
        try {
            PreparedStatement stmt = m_conn.prepareStatement(sqlQuery);
            stmt.setInt(1, id);
            res = stmt.executeQuery();
            weight = res.getFloat("weight");
            stmt.close();
        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
            return 0.0f;
        }

        return weight;
    }

    public int getUserLastWOID(final int id) {
        int woID = 0;
        ResultSet res;
        String sqlQuery = "SELECT last_workout FROM Users WHERE id=?";
        try {
            PreparedStatement stmt = m_conn.prepareStatement(sqlQuery);
            stmt.setInt(1, id);
            res = stmt.executeQuery();
            woID = res.getInt("last_workout");
            stmt.close();
        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
        }

        return woID;
    }

    public void setUserLastWOID(final int id, final int lastWOID) {
        String sqlQuery = "UPDATE Users SET last_workout=? WHERE id=?";
        try {
            PreparedStatement stmt = m_conn.prepareStatement(sqlQuery);
            stmt.setInt(1, lastWOID);
            stmt.setInt(2, id);
            if (stmt.executeUpdate() != 1) {
                System.err.println("User's last workout was not updated correctly.");
            }
        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    // Returns workout ID
    public int addWorkout(final int userID, final int year, final int month, final int day,
                          final float duration, final float distance, final float altitude) {
        java.sql.Date WODate = new java.sql.Date(year, month, day);
        int woID = 0;
        ResultSet res;
        String sqlInsertQuery = "INSERT INTO Workouts (" +
                "user_id," +
                "date," +
                "duration," +
                "distance," +
                "altitude" +
                ") VALUES (?, ?, ?, ?, ?)";
        String sqlSelectQuery = "SELECT id FROM Workouts WHERE " +
                "user_id=? AND " +
                "date=? AND " +
                "duration=? AND " +
                "distance=? AND " +
                "altitude=?";

        try {
            PreparedStatement stmt = m_conn.prepareStatement(sqlInsertQuery);
            stmt.setInt(1, userID);
            stmt.setDate(2, WODate);
            stmt.setFloat(3, duration);
            stmt.setFloat(4, distance);
            stmt.setFloat(5, altitude);

            if (stmt.executeUpdate() != 1) {
                System.err.println("Workout not added to database.");
            }

            stmt.close();

            // Pass back in the stuff we just created to get the right row ID
            // Look at a better way of doing this with OUTPUT clause of INPUT statement
            stmt = m_conn.prepareStatement(sqlSelectQuery);
            stmt.setInt(1, userID);
            stmt.setDate(2, WODate);
            stmt.setFloat(3, duration);
            stmt.setFloat(4, distance);
            stmt.setFloat(5, altitude);

            res = stmt.executeQuery();
            woID = res.getInt("id");
        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
        }

        return woID;
    }

    public void updateWorkout(final int WOID, final float duration, final float distance, final float altitude) {
        String sqlQuery = "UPDATE Workouts SET " +
                "duration = ?, " +
                "distance = ?, " +
                "altitude=? " +
                "WHERE id=? ";
        try {
            PreparedStatement stmt = m_conn.prepareStatement(sqlQuery);
            stmt.setFloat(1, duration);
            stmt.setFloat(2, distance);
            stmt.setFloat(3, altitude);
            stmt.setInt(4, WOID);

            int result = stmt.executeUpdate();
            System.err.println(Integer.toString(result) + " rows updated in updateWorkout().");
            if (result != 1) {
                System.err.println("Workout not updated in database.");
            }

            stmt.close();
        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
        }

    }

    public float getWorkoutAttribute(final WorkoutAttribute attribute, final int WOID) {
        ResultSet res;
        PreparedStatement stmt;
        String sqlQuery, columnLabel;
        float attrVal = 0.0f;
        if (workoutExists(WOID)) {
            try {
                switch (attribute) {
                    case DURATION:
                        sqlQuery = "SELECT duration FROM Workouts WHERE id=?";
                        columnLabel = "duration";
                        break;
                    case DISTANCE:
                        sqlQuery = "SELECT distance FROM Workouts WHERE id=?";
                        columnLabel = "distance";
                        break;
                    case ALTITUDE:
                        sqlQuery = "SELECT altitude FROM Workouts WHERE id=?";
                        columnLabel = "altitude";
                        break;
                    default:
                        return attrVal;
                }
                stmt = m_conn.prepareStatement(sqlQuery);
                stmt.setInt(1, WOID);
                res = stmt.executeQuery();
                attrVal = res.getFloat(columnLabel);
            }
            catch (final SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        else {
            System.err.println("Workout " + Integer.toString(WOID) + " does not exist. Cannot get distance.");
        }

        return attrVal;

    }

    public boolean workoutExists(final int WOID) {
        ResultSet res;
        String sqlQuery = "SELECT COUNT(*) as count FROM Workouts WHERE id=?";
        boolean exists = false;
        try {
            PreparedStatement stmt = m_conn.prepareStatement(sqlQuery);
            stmt.setInt(1, WOID);
            res = stmt.executeQuery();
            switch (res.getInt("count")) {
                case 0:
                    exists = false;
                    break;
                case 1:
                    exists = true;
                    break;
                default:
                    exists = true;
                    System.err.println("More than one workout for ID " +
                            Integer.toString(WOID) + ". Something isn't right.");
                    break;
            }

        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
        }

        return exists;
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
                    "    password_salt BLOB    NOT NULL," +
                    "    last_workout  INTEGER NOT NULL DEFAULT 0," +
                    "    created_at    DATE    NOT NULL" +
                    ")";
            if (!executeUpdate(sqlQuery)) {
                return false;
            }

            // Create workouts table
            sqlQuery = "CREATE TABLE WORKOUTS (" +
                    "    id        INTEGER PRIMARY KEY ASC AUTOINCREMENT NOT NULL," +
                    "    user_id   INTEGER NOT NULL REFERENCES USERS (id)," +
                    "    date      DATE    UNIQUE NOT NULL," +
                    "    duration  REAL    NOT NULL," + // seconds
                    "    distance  REAL    NOT NULL," + // metres
                    "    altitude  REAL    NOT NULL" + // metres
                    ")";

            if (!executeUpdate(sqlQuery)) {
                return false;
            }

            // Create friends table
            sqlQuery = "CREATE TABLE FRIENDS (" +
                    "    id            INTEGER  PRIMARY KEY ASC AUTOINCREMENT NOT NULL," +
                    "    sender        INTEGER  NOT NULL REFERENCES USERS (id)," +
                    "    receiver      INTEGER  REFERENCES USERS (id)," +
                    "    send_date     DATETIME NOT NULL," +
                    "    confirm_date  DATETIME DEFAULT NULL" +
                    ")";

            if (!executeUpdate(sqlQuery)) {
                return false;
            }

        }

        return true;
    }

}
