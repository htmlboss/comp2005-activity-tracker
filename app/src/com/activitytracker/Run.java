package com.activitytracker;

import java.util.Date;
import java.time.LocalTime;

/**
 * Used to logically instantiate a run.
 */
class Run {
    /**
     * The date the run occurred.
     */
    Date date;
    /**
     * The run's connection to the database. This is used to add data points and retrieve workout metadata.
     */
    DBManager dbManager;
    /**
     * The length of the run in seconds.
     */
    float duration;
    /**
     * The distance (in metres) that the user ran.
     */
    float distance;
    /**
     * The altitude (in metres) that the user climed throughout the run.
     */
    float altitude_ascended;
    /**
     * The altitude (in metres) that the user descended throughout their run.
     */
    float altitude_descended;
    /**
     * The number of calories that the user burned throughout their run.
     *
     * Currently this is not being used; it is for future features.
     */
    long caloriesBurned = 0;

    /**
     * The Run() constructor is used to retrieve workout information from the database and instantiate each
     * row of the Runs table in a logical format.
     *
     * @param dbManager The connection to the database.
     * @param rID The run ID used to retrieve information from the database.
     */
    Run (final DBManager dbManager, final int rID) {
        this.dbManager = dbManager;
        this.duration = this.dbManager.getRunFloatAttribute(RunAttribute.DURATION, rID);
        this.distance = this.dbManager.getRunFloatAttribute(RunAttribute.DISTANCE, rID);
        this.altitude_ascended = this.dbManager.getRunFloatAttribute(RunAttribute.ALTITUDE_ASCENDED, rID);
        this.altitude_descended = this.dbManager.getRunFloatAttribute(RunAttribute.ALTITUDE_DESCENDED, rID);
    }


    // Add workout to database
    public static void newRunDataPoint(final DBManager dbManager, final User user, final float duration,
                                  final Date date, final float distance, final float altitude) {
        int userID = user.getID();
        int rID;
        float altitude_ascended;
        float altitude_descended;

        if (duration == 0f && distance == 0f && altitude == 0f) {
            altitude_ascended = 0f;
            altitude_descended = 0f;
            rID = dbManager.newRun(
                    userID,
                    date.getYear(),
                    date.getMonth(),
                    date.getDay(),
                    duration,
                    distance,
                    altitude_ascended,
                    altitude_descended
            );
            user.setLastRID(rID);
            System.err.println("Run " + Integer.toString(rID) + " added to database.");
        } else {
            rID = user.getLastRID();
            if (dbManager.runExists(rID)) {
                altitude_ascended = dbManager.getRunFloatAttribute(RunAttribute.ALTITUDE_ASCENDED, rID);
                altitude_descended = dbManager.getRunFloatAttribute(RunAttribute.ALTITUDE_DESCENDED, rID);

                if (altitude < 0)
                    altitude_descended += -1*altitude;
                else
                    altitude_ascended += altitude;

                dbManager.setRun(rID, duration, distance, altitude_ascended, altitude_descended);
                System.err.println("Run " + Integer.toString(rID) + " exists in the database; updating...");
            } else {
                System.err.println("Run table and User table are inconsistent. No changes made.");
            }
        }
    }


    // Returns array of Workouts
    public static Run[] getRuns(final DBManager dbManager, final Date startDate, final Date endDate) {
        Run[] runs = null;

        return runs;
    }




}
