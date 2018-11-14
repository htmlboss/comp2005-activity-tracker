package com.activitytracker;

import java.util.Date;

enum Sex {
    MALE,
    FEMALE
}

class User {
    private long ID = 0;
    String name;
    String emailAddress;
    Date dateOfBirth;
    Sex sex;
    float height = 0.0f;
    float weight = 0.0f;
}
