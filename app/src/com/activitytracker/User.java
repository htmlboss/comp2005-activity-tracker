package com.activitytracker;

import javax.naming.AuthenticationException;
import javax.xml.transform.Result;
import java.util.Date;
import java.util.NoSuchElementException;


class User {
    public enum Sex {
        MALE,
        FEMALE
    }

    private int id;
    private String name;
    private String emailAddress;
    private Date dateOfBirth;
    private Sex sex;
    private float height;
    private float weight;
    private DBManager dbManager = new DBManager();

    User(final String emailAddress, final String plaintextPassword) throws AuthenticationException{

        if (this.dbManager.userExists(emailAddress)) {
            this.id = (int) dbManager.getUserAttribute(UserAttribute.ID, emailAddress);
            String passHash = (String) this.dbManager.getUserAttribute(UserAttribute.PASSWORD, this.id);
            if (new SecureString(plaintextPassword).equalString(passHash)) {
                this.name = (String) dbManager.getUserAttribute(UserAttribute.NAME, this.id);
                this.emailAddress = (String) dbManager.getUserAttribute(UserAttribute.EMAIL_ADDRESS, this.id);
                this.dateOfBirth = (Date) dbManager.getUserAttribute(UserAttribute.DATE_OF_BIRTH, this.id);
                this.sex = (User.Sex) dbManager.getUserAttribute(UserAttribute.SEX, this.id);
                this.height = (float) dbManager.getUserAttribute(UserAttribute.HEIGHT, this.id);
                this.weight = (float) dbManager.getUserAttribute(UserAttribute.WEIGHT, this.id);
            }
            else {
                throw new AuthenticationException("Incorrect password.");
            }
        }
        else {
            throw new NoSuchElementException("No such user exists.");
        }

    }

    public User createUser(final String name, final String emailAddress, final Date dateOfBirth,
                           final User.Sex sex, final float height, final float weight, final String plaintextPassword) {

        SecureString securePassword = new SecureString(plaintextPassword);

        this.dbManager.createUser(
                name,
                emailAddress,
                dateOfBirth,
                sex,
                height,
                weight,
                securePassword
        );

        try {
            return new User(emailAddress, plaintextPassword);
        }
        catch (final AuthenticationException e) {
            System.err.print(e.getMessage());
            System.err.print("Returned null User.");
            return null;
        }


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

    public void setEmailAddress(final String newEmailAddress) {
        if (!this.dbManager.setUserAttribute(UserAttribute.EMAIL_ADDRESS, this.id, newEmailAddress))
            System.err.println("User email address update failed.");
    }

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }

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
