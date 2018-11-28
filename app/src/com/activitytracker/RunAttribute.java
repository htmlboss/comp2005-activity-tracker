package com.activitytracker;

/**
 * This enumeration type is used to specify the behaviour of generalized methods, particularly in the DBManager class.
 */
public enum RunAttribute {
    /**
     * The cumulative distance the user has run (in metres).
     *
     * Used in DBManager#getRunFloatAttribute to specify that distance should be returned
     * and RunStats#computeMean().
     */
    DISTANCE,
    /**
     * The duration of the user's run (in seconds).
     *
     * Used in DBManager#getRunFloatAttribute to specify that duration should be returned
     * and RunStats#computeMean().
     */
    DURATION,
    /**
     * The cumulative altitude (in metres) that the user has climbed throughout their run.
     *
     * Used in DBManager#getRunFloatAttribute to specify that ascended altitude should be returned
     * and RunStats#computeMean().
     */
    ALTITUDE_ASCENDED,
    /**
     * The cumulative altitude (in metres) that the user has descended throughout their run.
     *
     * Used in DBManager#getRunFloatAttribute to specify that descended altitude should be returned
     * and RunStats#computeMean().
     */
    ALTITUDE_DESCENDED,
    /**
     * The average speed the user ran.
     *
     * Used in RunStats#computeMean().
     */
    SPEED
}
