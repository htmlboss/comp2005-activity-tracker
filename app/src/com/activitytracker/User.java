package com.activitytracker;

import java.util.Date;
import java.util.Vector;

class User {
    private long ID = 0;
    String name;
    Vector<User> friends;
    Vector<Long> pendingFriends;
    Vector<Device> devices;
    Vector<Workout> workouts;
    long weight, height;
    Date dateOfBirth;
}
