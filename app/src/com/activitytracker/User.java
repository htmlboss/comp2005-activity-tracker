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

    private int id;
    private String name;
    private String emailAddress;
    private Date dateOfBirth;
    private Sex sex;
    private float height;
    private float weight;
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

    public static void createUser(final DBManager dbManager, final String name, final String emailAddress, final int DOBYear,
                                  final int DOBMonth, final int DOBDay, final User.Sex sex, final float height,
                                  final float weight, final String plaintextPassword) {

        SecureString securePassword = new SecureString(plaintextPassword);


        dbManager.createUser(
                name,
                emailAddress,
                DOBYear,
                DOBMonth,
                DOBDay,
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

    public int getLastWOID() { return this.dbManager.getUserLastWOID(this.id); }

    public void setLastWOID(final int woID) { this.dbManager.setUserLastWOID(this.id, woID); }

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
