package com.activitytracker;

/**
 * This enumeration type is used to specify the behaviour of generalized methods, particularly in the DBManager class.
 */
public enum UserAttribute {
    /**
     * Currently not used as no generalized method retrieves the user's ID.
     */
    ID,
    /**
     * The user's full name.
     *
     * Used in DBManager#getUserStringAttribute to specify that the user's name should be returned.
     */
    NAME,
    /**
     * The user's email address.
     *
     * Used in DBManager#getUserStringAttribute to specify that the user's email address should be returned.
     */
    EMAIL_ADDRESS,
    /**
     * Currently not used as no generalized method retrieves the user's DOB.
     */
    DATE_OF_BIRTH,
    /**
     * Currently not used as no generalized method retrieves the user's sex.
     */
    SEX,
    /**
     * The user's weight (in kilograms).
     *
     * Used in DBManager#getUserFloatAttribute to specify that the user's email weight should be returned.
     */
    WEIGHT,
    /**
     * The user's height (in metres).
     *
     * Used in DBManager#getUserFloatAttribute to specify that the user's email height should be returned.
     */
    HEIGHT,
    /**
     * The user's encrypted password hash.
     *
     * Used in DBManager#getUserStringAttribute to specify that the user's password hash should be returned.
     */
    PASSWORD,
    /**
     * Currently not used as no generalized method retrieves the user's password encryption salt.
     */
    SALT
}
