package com.activitytracker;

import java.util.Date;
import java.time.LocalTime;


class Workout {
    Date date;
    DBManager dbManager;
    float duration, distance, altitude;
    long caloriesBurned = 0;

    Workout (final DBManager dbManager, final int woID) {
        this.dbManager = dbManager;
        this.duration = this.dbManager.getWorkoutAttribute(WorkoutAttribute.DURATION, woID);
        this.distance = this.dbManager.getWorkoutAttribute(WorkoutAttribute.DISTANCE, woID);
        this.altitude = this.dbManager.getWorkoutAttribute(WorkoutAttribute.ALTITUDE, woID);
    }


    // Add workout to database
    public static void addNewWorkoutDataPoint(final DBManager dbManager, final User user, final float duration,
                                  final Date date, final float distance, final float altitude) {
        int userID = user.getID();
        int woID;

        if (duration == 0f && distance == 0f && altitude == 0f) {
            woID = dbManager.addWorkout(
                    userID,
                    date.getYear(),
                    date.getMonth(),
                    date.getDay(),
                    duration,
                    distance,
                    altitude
            );
            user.setLastWOID(woID);
            System.err.println("Workout " + Integer.toString(woID) + " added to database.");
        } else {
            woID = user.getLastWOID();
            if (dbManager.workoutExists(woID)) {
                dbManager.updateWorkout(woID, duration, distance, altitude);
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
