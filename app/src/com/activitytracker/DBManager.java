package com.activitytracker;

import java.sql.*;
import java.util.Calendar;

/**
 * Singleton class for the database. All classes and methods that interact with the database will use a
 * method in this class.
 *
 * Many times we are faced with the "chicken and egg" problem where we wish to create an object that is
 * populated with information from the database. So the question one faces is, "does the object's constructor
 * query the database (through the DBManager class, of course) for each attribute of the object that it
 * wishes to retrieve, or do we directly interact with a DBManager method which will then return a User or
 * Workout object, for example?" We have decided to use the former methodology, with DBManager methods being as
 * general as possible, and often accepting enum types which then are put into a switch to create the
 * specific SQL query we wish to execute. This works best when all data returned is of the same data type
 * (for example, the Workout class will have three float attributes at the time of writing so we use one method
 * with return type of float for returning Workout attributes). This does not work as well when the object
 * requires data of multiple types --- for example, the User class. In this case, we have split the DBManager
 * methods into a single method for each attribute being returned.
 *
 * Polymorphism could theoretically be used here to simply have a return type of Object, however this is not
 * flexible and requires casting \em all returned data to the correct type in the invoking method.
 */
class DBManager {
    /**
     * The \em m_conn variable in the DBManager class is initially assigned the value of \em null.
     *
     * When DBManager#init() is invoked, it is made to be the connection to the database and is subsequently used
     * each time a new SQL statement is created.
     */
    private Connection m_conn = null;

    /**
     *  Creates a new DBManager object.
     *
     *  This should only be called once, from the main program, as DBManager is meant to be a \em singleton class.
     *
     *  This constructor takes no parameters as verification of the SQLite database is done in the init() method of this
     *  class, which returns information about whether the initialization was successful or not.
     */
    DBManager() {
    }

    /**
     * Adds a row for a user to the Users table in the SQLite database for the app.
     *
     * Requires that the database tables exist and are in the correct format.
     * If the user exists in the database this method raises an AssertionError exception.
     *
     * @param name %User's name
     * @param emailAddress %User's email address; used to authenticate
     * @param DOBYear The year the user was born
     * @param DOBMonth The month the user was born
     * @param DOBDay The day of month the user was born
     * @param sex The user's sex; is either User.Sex.MALE or User.Sex.FEMALE
     * @param height Floating point number of the user's height in metres
     * @param weight Floating point number of the user's weight in kilograms
     * @param securePassword A SecureString object containing the user's password, encrypted
     */
    public void createUser(final String name, final String emailAddress, final int DOBYear,
                           final int DOBMonth, final int DOBDay, final User.Sex sex, final float height,
                           final float weight, final SecureString securePassword) throws AssertionError {

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
     * The DBManager::userExists() method is designed to facilitate the user experience (UX) design choice of users creating one
     * account to the app and logging in with an existing account for future use. This maintains saved (persistent) data
     * and helps enforce the unique constraint placed on the \em email_address field in the database (again, as users are
     * authenticating using their email address as a user name to identify themselves).
     *
     * @param emailAddress The user's email address for which we are checking existence. We use email address here
     *                     because this is what the user uses to log in to the app.
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

    /**
     * As we are using the user's email address as their identifying attribute, they will supply this when they log in.
     * Hence, as the database relates everything to the user's unique ID, we must retrieve this ID given the email
     * address.
     *
     * The logic behind this method relies on the database Users table structure making \em email_address a unique field.
     *
     * @param emailAddress The user's email address with which they authenticate.
     *
     * @return This method returns a unique integer corresponding to the row in the database's Users table that stores
     * user information for user with email address \em emailAddress.
     */
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

    /**
     * This method retrieves a string, varchar, text, or char field, when applicable, from the database's Users table.
     *
     * This method accepts a UserAttribute enumeration type to specify what attribute it is returning from the database.
     * Only certain attributes are accepted by this method, namely those that are stored as string-like values.
     * Attributes stored as other data types should use the appropriate accessor method.
     *
     * @param attribute \parblock
     *  The attribute that the method is supposed to query the DB for and return the value of. Note that only certain
     *  UserAttribute types are supported in this method.
     *    - When \em attribute is UserAttribute.PASSWORD, this method retrieves the user's encrypted password from the
     *      database. Typically this will be used in the following sequence of calls:
     *          -# %User attempts to authenticate with email and password
     *          -# Their unique ID is retrieved from the database using DBManager#getUserIDByEmail()
     *          -# Their ID is used to retrieve the hash of their password (i.e., this method is called)
     *          -# The returned string from this method is compared a SecureString generated from the candidate
     *             password supplied by the user when authenticating.
     *    - When \em attribute is UserAttribute.NAME, this method retrieves the user's full name from the database
     *      (e.g., "John Doe").
     *    - When \em attribute is UserAttribute.EMAIL_ADDRESS, this method retrieves the user's email address from the
     *      database. Note that this is likely somewhat redundant as the user will always be required to authenticate by
     *      providing their email address and hence it will already be available to the User constructor, which is
     *      likely what is invoking this method.
     *  \endparblock
     * @param id Unique ID used to associate information in the database to this user.
     *
     * @return This method returns a string containing attribute specified by the \em attribute parameter for the user
     *         specified by the \em id parameter.
     */
    public String getUserStringAttribute(final UserAttribute attribute, final int id) {
        String name;
        ResultSet res;
        String sqlQuery, columnLabel;
        switch (attribute) {
            case PASSWORD:
                columnLabel = "password_hash";
                sqlQuery = "SELECT " + columnLabel + " FROM Users WHERE id=?";
                break;
            case NAME:
                columnLabel = "name";
                sqlQuery = "SELECT " + columnLabel + " FROM Users WHERE id=?";
                break;
            case EMAIL_ADDRESS:
                columnLabel = "email_address";
                sqlQuery = "SELECT " + columnLabel + " FROM Users WHERE id=?";
                break;
            default:
                throw new AssertionError("Incorrect UserAttribute enumeration type passed to method.");
        }
        try {
            PreparedStatement stmt = m_conn.prepareStatement(sqlQuery);
            stmt.setInt(1, id);
            res = stmt.executeQuery();
            name = res.getString(columnLabel);

            stmt.close();
        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }

        return name;
    }

    /**
     * Retrieves a user's attribute in floating point format, when applicable, from the database's Users table.
     *
     * This method accepts a UserAttribute enumeration type to specify what attribute it is returning from the database.
     * Only certain attributes are accepted by this method, namely those that are stored as real values. Attributes
     * stored as other data types should use the appropriate accessor method.
     *
     * @param attribute \parblock
     *  The attribute that the method is supposed to query the DB for and return the value of. Note that only certain
     *  UserAttribute types are supported in this method.
     *    - When \em attribute is UserAttribute.WEIGHT, this method retrieves the user's weight from the database.
     *    - When \em attribute is UserAttribute.HEIGHT, this method retrieves the user's height from the database.
     *  \endparblock
     * @param id Unique ID used to associate information in the database to this user.
     *
     * @return Returns a floating point number corresponding to the UserAttribute passed to the method, for the user
     *         specified by \em id.
     */
    public float getUserFloatAttribute(final UserAttribute attribute, final int id) {
        float attrVal;
        ResultSet res;
        String sqlQuery, columnLabel;
        switch (attribute) {
            case WEIGHT:
                columnLabel = "weight";
                sqlQuery = "SELECT " + columnLabel + " FROM Users WHERE id=?";
                break;
            case HEIGHT:
                columnLabel = "height";
                sqlQuery = "SELECT " + columnLabel + " FROM Users WHERE id=?";
                break;
            default:
                throw new AssertionError("Incorrect UserAttribute enumeration type passed to method.");
        }
        try {
            PreparedStatement stmt = m_conn.prepareStatement(sqlQuery);
            stmt.setInt(1, id);
            res = stmt.executeQuery();
            attrVal = res.getFloat(columnLabel);

            stmt.close();
        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
            return 0.0f;
        }

        return attrVal;
    }

    /**
     * Retrieves the user's date of birth (DOB) from the database.
     *
     * At the time of writing, this method is only being used in the User constructor.
     *
     * @param id Unique ID used to associate information in the database to this user.
     *
     * @return This method returns a Date object containing the user's DOB (i.e., year, month, day).
     */
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

    /**
     * Retrieves the user's gender from the database.
     *
     * We have chosen to represent gender in the SQLite database with the data type %BIT(1), where 1 denotes male and 0
     * denotes female. Hence, if the database contains 1 this method returns User.Sex.MALE and if the database contains
     * 0 then this method returns User.Sex.FEMALE.
     *
     * At the time of writing, this method is only being used in the User constructor.
     *
     * @param id Unique ID used to associate information in the database to this user.
     *
     * @return This method returns a User.Sex enumeration type corresponding to the user's gender.
     */
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

    /**
     * Retrieves a byte array containing the salt used to encrypt the user's password from the database.
     *
     * This is necessary because to compare a candidate password supplied by a user to a known (encrypted) password
     * stored in the database, we must encrypt the new candidate password using the same salt as was originally used.
     *
     * @param id Unique ID used to associate information in the database to this user.
     *
     * @return This method returns a byte array containing the user's password encryption salt.
     */
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

    /**
     * Retrieves the last workout ID that the user added as an integer from the database.
     *
     * This is used because of the format in which the data is supplied. As the only way to denote a new workout is
     * by recieving (0, 0, 0) in the input file, if the input is \em not (0, 0, 0), we need to update the previously
     * added workout with the latest line. Hence we need some way of storing an identifier for this workout. As this
     * is unique to each user, we have chosen to store this in the Users table of the database.
     *
     * @param id Unique ID used to associate information in the database to this user.
     * @return An integer corresponding to the last row in the Workouts table that the user created.
     */
    public int getUserLastRID(final int id) {
        int rID = 0;
        ResultSet res;
        String columnLabel = "last_run";
        String sqlQuery = "SELECT " + columnLabel + " FROM Users WHERE id=?";
        try {
            PreparedStatement stmt = m_conn.prepareStatement(sqlQuery);
            stmt.setInt(1, id);
            res = stmt.executeQuery();
            rID = res.getInt(columnLabel);
            stmt.close();
        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
        }

        return rID;
    }

    /**
     * Updates a user's last run ID in the database.
     *
     * This method will be used to update the run that a particular user last created. This is used when creating
     * new run as the format of the input file requires that we maintain a record of what run we must update
     * if the next line in the file is \em not (0, 0, 0).
     *
     * See getUserLastRID() for more information on the user of the \em last_run field in the database.
     *
     * @param id Unique ID used to associate information in the database to this user.
     * @param lastWOID Integer corresponding to the last row in the Workouts table that the user with ID \em id created.
     */
    public void setUserLastRID(final int id, final int lastRID) {
        String sqlQuery = "UPDATE Users SET last_run=? WHERE id=?";
        try {
            PreparedStatement stmt = m_conn.prepareStatement(sqlQuery);
            stmt.setInt(1, lastRID);
            stmt.setInt(2, id);
            if (stmt.executeUpdate() != 1) {
                System.err.println("User's last run was not updated correctly.");
            }
        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Creates a new row in the Runs table with the attributes provided as parameters.
     *
     * In particular, this method will be called when Run#newRunDataPoint() receives (0, 0, 0) for
     * (\em duration, \em distance, \em altitude).
     *
     * @param userID Unique ID used to associate information in the database to this user.
     * @param year Year that the run was completed.
     * @param month Month that the run was completed (1-12).
     * @param day Day that the run was completed (1-31).
     * @param duration Duration of the run in seconds.
     * @param distance Distance ran in metres.
     * @param altitude_ascended Cumulative altitude climbed in metres.
     * @param altitude_descended Cumulative altitude descended in metres.
     *
     * @return Returns a unique integer corresponding to the new row in the SQLite Workouts table by which the new
     *         entry can be identified.
     */
    public int newRun(final int userID, final int year, final int month, final int day,
                          final float duration, final float distance, final float altitude_ascended,
                          final float altitude_descended) {
        java.sql.Date RDate = new java.sql.Date(year, month, day);
        int rID = 0;
        ResultSet res;
        String sqlInsertQuery = "INSERT INTO Runs (" +
                "user_id," +
                "date," +
                "duration," +
                "distance," +
                "altitude_ascended," +
                "altitude_descended" +
                ") VALUES (?, ?, ?, ?, ?, ?)";
        String sqlSelectQuery = "SELECT id FROM Runs WHERE " +
                "user_id=? AND " +
                "date=? AND " +
                "duration=? AND " +
                "distance=? AND " +
                "altitude_ascended=? AND " +
                "altitude_descended=?";

        try {
            PreparedStatement stmt = m_conn.prepareStatement(sqlInsertQuery);
            stmt.setInt(1, userID);
            stmt.setDate(2, RDate);
            stmt.setFloat(3, duration);
            stmt.setFloat(4, distance);
            stmt.setFloat(5, altitude_ascended);
            stmt.setFloat(6, altitude_descended);

            if (stmt.executeUpdate() != 1) {
                System.err.println("Run not added to database.");
            }

            stmt.close();

            // Pass back in the stuff we just created to get the right row ID
            // Look at a better way of doing this with OUTPUT clause of INPUT statement
            stmt = m_conn.prepareStatement(sqlSelectQuery);
            stmt.setInt(1, userID);
            stmt.setDate(2, RDate);
            stmt.setFloat(3, duration);
            stmt.setFloat(4, distance);
            stmt.setFloat(5, altitude_ascended);
            stmt.setFloat(6, altitude_descended);

            res = stmt.executeQuery();
            rID = res.getInt("id");
        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
        }

        return woID;
    }

    /**
     * Updates a run entry in the database as new information becomes available from the input file.
     *
     * In particular, this method is called when Run#newRunDataPoint() receives non-(0, 0, 0) input for
     * (\em duration, \em distance, \em altitude).
     *
     * This method will not be called directly by the application, rather it is called from
     * Run#newRunDataPoint(). Hence that method will take care of adding/subtracting to/from the current stored
     * values for \em duration, \em distance, and \em altitude --- here we just take the input and put it in the
     * database.
     *
     * @param rID Unique ID used to identify a run in the database.
     * @param duration The number of seconds the user's run lasted.
     * @param distance The cumulative number of metres the user ran.
     * @param altitude_ascended The cumulative number of metres the user climbed.
     * @param altitude_descended The cumulative number of metres the user descended.
     */
    public void setRun(final int rID, final float duration, final float distance,
                              final float altitude_ascended, final float altitude_descended) {
        String sqlQuery = "UPDATE Runs SET " +
                "duration = ?, " +
                "distance = ?, " +
                "altitude_ascended=?, " +
                "altitude_descended=? " +
                "WHERE id=? ";
        try {
            PreparedStatement stmt = m_conn.prepareStatement(sqlQuery);
            stmt.setFloat(1, duration);
            stmt.setFloat(2, distance);
            stmt.setFloat(3, altitude_ascended);
            stmt.setFloat(4, altitude_descended);
            stmt.setInt(5, rID);

            int result = stmt.executeUpdate();
            System.err.println(Integer.toString(result) + " rows updated in setRun().");
            if (result != 1) {
                System.err.println("Run not updated in database.");
            }

            stmt.close();
        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
        }

    }

    /**
     * Retrieves a run's attribute as a floating point number, where applicable, from the database.
     *
     * This method accepts a RunAttribute enumeration type to specify what attribute it is returning from the
     * database. Only certain attributes are accepted by this method, namely those that are stored as real values.
     * Attributes stored as other data types should use the appropriate accessor method.
     *
     * @param attribute \parblock
     *    The attribute that the method is supposed to query the DB for and return the value of. Note that only certain
     *    RunAttribute types are supported in this method.
     *      - When \em attribute is RunAttribute.DURATION, the run's duration is returned.
     *      - When \em attribute is RunAttribute.DISTANCE, the run's cumulative distance is returned in metres.
     *      - When \em attribute is RunAttribute.ALTITUDE_ASCENDED, the run's cumulative altitude climbed is
     *        returned in metres
     *      - When \em attribute is RunAttribute.ALTITUDE_DESCENDED, the run's cumulative altitude descended is
     *        returned in metres
     *   \endparblock
     * @param WOID Unique ID corresponding to the row in the Runs table that we wish to query. If such an ID does
     *             not exist, \em 0.0f will be returned.
     *
     * @return This method returns a float containing run attribute as specified by the \em attribute parameter.
     */
    public float getRunFloatAttribute(final RunAttribute attribute, final int rID) {
        ResultSet res;
        PreparedStatement stmt;
        String sqlQuery, columnLabel;
        float attrVal = 0.0f;
        switch (attribute) {
            case DURATION:
                columnLabel = "duration";
                sqlQuery = "SELECT " + columnLabel + " FROM Runs WHERE id=?";
                break;
            case DISTANCE:
                columnLabel = "distance";
                sqlQuery = "SELECT " + columnLabel + " FROM Runs WHERE id=?";
                break;
            case ALTITUDE_ASCENDED:
                columnLabel = "altitude_ascended";
                sqlQuery = "SELECT " + columnLabel + " FROM Runs WHERE id=?";
                break;
            case ALTITUDE_DESCENDED:
                columnLabel = "altitude_descended";
                sqlQuery = "SELECT " + columnLabel + " FROM Runs WHERE id=?";
                break;
            default:
                return attrVal;
        }
        if (workoutExists(WOID)) {
            try {
                stmt = m_conn.prepareStatement(sqlQuery);
                stmt.setInt(1, rID);
                res = stmt.executeQuery();
                attrVal = res.getFloat(columnLabel);
            }
            catch (final SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        else {
            System.err.println("Run " + Integer.toString(WOID) + " does not exist. Cannot get " + columnLabel + ".");
        }

        return attrVal;

    }

    /**
     * Determines if a given run ID exists in the database.
     *
     * @param rID Unique ID corresponding to the row in the Runs table that we wish to check exists.
     *
     * @return This method returns True if the run row with ID \em WOID exists in the database, or False otherwise.
     */
    public boolean runExists(final int rID) {
        ResultSet res;
        String sqlQuery = "SELECT COUNT(*) as count FROM Runs WHERE id=?";
        boolean exists = false;
        try {
            PreparedStatement stmt = m_conn.prepareStatement(sqlQuery);
            stmt.setInt(1, rID);
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
                    System.err.println("More than one run for ID " +
                            Integer.toString(WOID) + ". Something isn't right.");
                    break;
            }

        }
        catch (final SQLException e) {
            System.err.println(e.getMessage());
        }

        return exists;
    }

    /**
     * A wrapper method for processing \em safe SQL queries.
     *
     * By safe we mean that the SQL query string is entirely hard-coded in the program source code. In other words, no
     * user input is added. This is an important distinction as the former may leave the application vulnerable to
     * SQL injection.
     *
     * In such cases, a SQL PreparedStatement should be used.
     *
     * @param sqlQuery The SQL code to be executed. Must be a \em SELECT statement.
     *
     * @return This method returns a ResultSet containing the returned row(s) and/or column(s) of the SQL query that
     *         was executed.
     */
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

    /**
     * A wrapper method for processing \em safe SQL queries.
     *
     * By safe we mean that the SQL query string is entirely hard-coded in the program source code. In other words, no
     * user input is added. This is an important distinction as the former may leave the application vulnerable to
     * SQL injection.
     *
     * In such cases, a SQL PreparedStatement should be used.
     *
     * @param sqlQuery The SQL code to be executed. Must be an \em INSERT or \em UPDATE statement.
     *
     * @return This method returns a boolean indicating if the query was successful.
     */
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

    /**
     * Returns a boolean value depending on whether or not the database is populated.
     *
     * This is done by retrieving tables in the database and checking if this iterator has a next(). If not then
     * there are no tables in the database and we consider it to be empty.
     *
     * @return Returns True if there are tables in the database, False otherwise.
     */
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

    /**
     * Initializes a connection to the SQLite database.
     *
     * As no work is done in the DBManager() constructor, this method should be called immediately after creating the
     * single instance of DBManager that the application is to use.
     *
     * This method will attempt to connect to the database file specified by the \em dbURL parameter, creating the file
     * and all required tables if it/they do not exist. You are encouraged to view the source code of this method for
     * more information about the database schema used.
     *
     * If all of the above is successful, the method returns True. Otherwise, False is returned.
     *
     * @param dbURL A file system path to the SQLite database file.
     *
     * @return This method returns True if the database can be initialized, or False otherwise.
     */
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
            sqlQuery = "CREATE TABLE RUNS (" +
                    "    id                  INTEGER PRIMARY KEY ASC AUTOINCREMENT NOT NULL," +
                    "    user_id             INTEGER NOT NULL REFERENCES USERS (id)," +
                    "    date                DATE    UNIQUE NOT NULL," +
                    "    duration            REAL    NOT NULL," + // seconds
                    "    distance            REAL    NOT NULL," + // metres
                    "    altitude_ascended   REAL    NOT NULL," + // metres
                    "    altitude_descended  REAL    NOT NULL" + // metres
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
