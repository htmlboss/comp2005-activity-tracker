package com.activitytracker;

import javax.naming.AuthenticationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

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
            try {
                Run.bulkImport(dbManager, john, "/Users/jacobhouse/Google Drive File Stream/My Drive/Documents/Courses/Computer Science/COMP-2005 Software Engineering/Final Project/comp2005-activity-tracker/app/InputWO.csv");
            }
            catch (final IOException e) {
                System.err.println(e.getMessage());
            }
        }
        else {
            System.out.println("John is null. Cannot execute phase 2.");
        }

        // Iteration 2 ends here
        // Iteration 3 begins here

        Date date = null;
        DateFormat sourceFormat = new SimpleDateFormat("dd-MM-yyyy");

        try {
            date = sourceFormat.parse("01-01-2018");
        }
        catch (final ParseException e) {
            System.err.println(e.getMessage());
        }

        Vector<Run> runs = Run.getRuns(dbManager, john, date, new Date());

        if (runs == null) {
            System.out.println("Runs is null.");
        } else if (runs.size() == 0)
            System.err.println("No runs in vector.");
        else
            for (Run run : runs) {
                System.out.println("Retrieved run with ID " + Integer.toString(run.getID()));
            }


        // Iteration 3 ends here

    }

}
