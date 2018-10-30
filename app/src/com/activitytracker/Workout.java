package com.activitytracker;

import java.util.Date;
import java.time.LocalTime;

enum Activity {
    WALK, RUN, SWIM, SKI, WEIGHTS, BASEBALL, BIKE
}

class Workout {
    Date data;
    LocalTime startTime, endTime;
    long caloriesBurned = 0;
    Activity activity;
}
