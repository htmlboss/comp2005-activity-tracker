package com.activitytracker;

import java.util.Date;
import java.time.LocalTime;


class Workout {
    Date date;
    DBManager dbManager;
    float duration, distance, altitude_ascended, altitude_descended;
    long caloriesBurned = 0;

    Workout (final DBManager dbManager, final int woID) {
        this.dbManager = dbManager;
        this.duration = this.dbManager.getWorkoutFloatAttribute(WorkoutAttribute.DURATION, woID);
        this.distance = this.dbManager.getWorkoutFloatAttribute(WorkoutAttribute.DISTANCE, woID);
        this.altitude_ascended = this.dbManager.getWorkoutFloatAttribute(WorkoutAttribute.ALTITUDE_ASCENDED, woID);
        this.altitude_descended = this.dbManager.getWorkoutFloatAttribute(WorkoutAttribute.ALTITUDE_DESCENDED, woID);
    }


    // Add workout to database
    public static void newWorkoutDataPoint(final DBManager dbManager, final User user, final float duration,
                                  final Date date, final float distance, final float altitude) {
        int userID = user.getID();
        int woID;
        float altitude_ascended, altitude_descended;

        if (duration == 0f && distance == 0f && altitude == 0f) {
            altitude_ascended = 0f;
            altitude_descended = 0f;
            woID = dbManager.newWorkout(
                    userID,
                    date.getYear(),
                    date.getMonth(),
                    date.getDay(),
                    duration,
                    distance,
                    altitude_ascended,
                    altitude_descended
            );
            user.setLastWOID(woID);
            System.err.println("Workout " + Integer.toString(woID) + " added to database.");
        } else {
            woID = user.getLastWOID();
            if (dbManager.workoutExists(woID)) {
                altitude_ascended = dbManager.getWorkoutFloatAttribute(WorkoutAttribute.ALTITUDE_ASCENDED, woID);
                altitude_descended = dbManager.getWorkoutFloatAttribute(WorkoutAttribute.ALTITUDE_DESCENDED, woID);

                if (altitude < 0)
                    altitude_descended += -1*altitude;
                else
                    altitude_ascended += altitude;

                dbManager.setWorkout(woID, duration, distance, altitude_ascended, altitude_descended);
                System.err.println("Workout " + Integer.toString(woID) + " exists in the database; updating...");
            } else {
                System.err.println("Workout table and User table are inconsistent. No changes made.");
            }
        }
    }


    // Returns array of Workouts
    public static Workout[] getWorkouts(final DBManager dbManager, final Date startDate, final Date endDate) {
        Workout[] workouts = null;

        return workouts;
    }




}
