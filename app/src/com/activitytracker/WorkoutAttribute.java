package com.activitytracker;

/**
 * This enumeration type is used to specify the behaviour of generalized methods, particularly in the DBManager class.
 */
public enum WorkoutAttribute {
    /**
     * The cumulative distance the user has run (in metres).
     *
     * Used in DBManager#getWorkoutFloatAttribute to specify that distance should be returned.
     */
    DISTANCE,
    /**
     * The duration of the user's run (in seconds).
     *
     * Used in DBManager#getWorkoutFloatAttribute to specify that duration should be returned.
     */
    DURATION,
    /**
     * The cumulative altitude (in metres) that the user has climbed throughout their run.
     *
     * Used in DBManager#getWorkoutFloatAttribute to specify that ascended altitude should be returned.
     */
    ALTITUDE_ASCENDED,
    /**
     * The cumulative altitude (in metres) that the user has descended throughout their run.
     *
     * Used in DBManager#getWorkoutFloatAttribute to specify that descended altitude should be returned.
     */
    ALTITUDE_DESCENDED
}
