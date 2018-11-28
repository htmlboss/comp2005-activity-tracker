package com.activitytracker;

import javax.naming.AuthenticationException;
import java.util.Date;
import java.util.NoSuchElementException;


class User {
    /**
     * Used to represent whether the user is male or female.
     */
    public enum Sex {
        /**
         * Used to represent that the user is male.
         *
         * Recall from the source code included in DBManager#init() that sex is stored in the database using a data
         * type of BIT(1). If the user is female, we store this in the database by populating this field with a \em 1.
         */
        MALE,
        /**
         * Used to represent that the user is female.
         *
         * Recall from the source code included in DBManager#init() that sex is stored in the database using a data
         * type of BIT(1). If the user is female, we store this in the database by populating this field with a \em 0.
         */
        FEMALE
    }

    /**
     * The user's ID, used by the database to associate workouts with a user.
     */
    private int id;
    /**
     * The user's full name (\em e.g., Johnathan Doe).
     */
    private String name;
    /**
     * The email address (\em e.g., jondoe@mac.com) that the user entered when registering for the app. This is used
     * to authenticate the user at login.
     */
    private String emailAddress;
    /**
     * The user's date of birth (\em e.g., 12-12-1998).
     */
    private Date dateOfBirth;
    /**
     * The user's sex. Can be one of User.Sex.MALE or User.Sex.FEMALE.
     */
    private Sex sex;
    /**
     * The user's height in metres.
     */
    private float height;
    /**
     * The user's weight in kilograms.
     */
    private float weight;
    /**
     * An instance of DBManager with which we perform any DB accesses required to retrieve or set user
     * attributes in the database.
     */
    private DBManager dbManager = null;

    User(final DBManager dbManager, final String emailAddress, final String plaintextPassword) throws AuthenticationException{

        this.dbManager = dbManager;
        if (this.dbManager.userExists(emailAddress)) {

            this.id = dbManager.getUserIDByEmail(emailAddress);

            String passHash = this.dbManager.getUserStringAttribute(UserAttribute.PASSWORD, this.id);
            byte[] passSalt = this.dbManager.getUserPassSalt(this.id);

            SecureString candidatePassword = new SecureString(plaintextPassword, passSalt);

            if (candidatePassword.equalString(passHash)) {


                this.name = this.dbManager.getUserStringAttribute(UserAttribute.NAME, this.id);
//                this.emailAddress = this.dbManager.getEmailAddress(this.id);
                this.emailAddress = emailAddress;
                this.dateOfBirth = this.dbManager.getDateOfBirth(this.id);
                this.sex = this.dbManager.getUserSex(this.id);
                this.height = this.dbManager.getUserFloatAttribute(UserAttribute.HEIGHT, this.id);
                this.weight = this.dbManager.getUserFloatAttribute(UserAttribute.WEIGHT, this.id);

                System.out.println("Authentication succeeded for " + this.name);

            }
            else {

                throw new AuthenticationException("Incorrect password.");

            }
        }
        else {

            throw new NoSuchElementException("No such user exists.");

        }

    }

    /**
     * Generates a SecureString for the plain text password provided by the user and passes this, along
     * with the rest of the user's information, to DBManager for entry in the database.
     *
     * @param dbManager An instance of DBManager with which we access the database to store the new user.
     * @param name The new user's full name (\em e.g., Johnathan Doe).
     * @param emailAddress The new user's email address (\em e.g., jondoe@mac.com) used to register.
     * @param dateOfBirth
     * @param sex
     * @param height
     * @param weight
     * @param plaintextPassword
     */
    public static void createUser(final DBManager dbManager, final String name, final String emailAddress,
                                  final Date dateOfBirth, final User.Sex sex, final float height,
                                  final float weight, final String plaintextPassword) {

        SecureString securePassword = new SecureString(plaintextPassword);


        dbManager.createUser(
                name,
                emailAddress,
                dateOfBirth,
                sex,
                height,
                weight,
                securePassword
        );

    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }

    public int getLastRID() { return this.dbManager.getUserLastRID(this.id); }

    public void setLastRID(final int rID) { this.dbManager.setUserLastRID(this.id, rID); }

    public Sex getSex() {
        return this.sex;
    }

    public float getHeight() {
        return this.height;
    }

    public float getWeight() {
        return this.weight;
    }
}
