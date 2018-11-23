package com.activitytracker;

import javax.naming.AuthenticationException;
import java.util.Date;

public class Iteration3Test {

    public static void main(String[] args) {

        // Iteration 1 begins here

        User john = null;

        DBManager dbManager = new DBManager();
        if (!dbManager.init("data.db")) {
            System.err.println("Failed to initialize DBManager");
            System.exit(1);
        }

        System.out.println("Attempting to create user...");

        if (!dbManager.userExists("jdoe@mac.com"))
            User.createUser(
                    dbManager,
                    "John Doe",
                    "jdoe@mac.com",
                    1997,
                    12,
                    12,
                    User.Sex.MALE,
                    1.6764f,
                    54.4310844f,
                    "My Very Secure Password"
            );
        else
            System.out.println("User already exists.");



        if (dbManager.userExists("jdoe@mac.com"))
            System.out.println("John Doe was created!");
        else
            System.out.println("User was NOT created.");


        System.out.println("Testing incorrect password...");

        try {
            john = new User(dbManager,"jdoe@mac.com", "Some Incorrect Password");
        }
        catch (final AuthenticationException e) {
            System.out.println("Incorrect password used; authentication failed.");
        }

        System.out.println("Authenticating user...");

        try {
            john = new User(dbManager,"jdoe@mac.com", "My Very Secure Password");
        }
        catch (final AuthenticationException e) {
            System.out.println("Test failed; user could not be authenticated.");
        }

        // Iteration 1 ended here

        // Iteration 2 begins here

        if (john !=  null) {
            Date today = new Date();
            Workout.newRunDataPoint(dbManager, john, 0f, today, 0f, 0f);
            Workout.newRunDataPoint(dbManager, john, 30f, today, 100f, 5f);
            Workout.newRunDataPoint(dbManager, john, 60f, today, 210f, 2f);
            Workout.newRunDataPoint(dbManager, john, 90f, today, 330f, -2f);
        }
        else {
            System.out.println("John is null. Cannot execute phase 2.");
        }

        // Iteration 2 ends here

    }

}
