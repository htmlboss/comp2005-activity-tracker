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

        if (!dbManager.userExists("jdoe@mac.com")) {
            Date dob = null;
            DateFormat sourceFormat = new SimpleDateFormat("dd-MM-yyyy");
            try {
                dob = sourceFormat.parse("03-04-1978");
            } catch (final ParseException e) {
                System.err.println(e.getMessage());
            }

            User.createUser(
                    dbManager,
                    "John Doe",
                    "jdoe@mac.com",
                    dob,
                    User.Sex.MALE,
                    1.6764f,
                    54.4310844f,
                    "My Very Secure Password"
            );
        }
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
        else {
            for (Run run : runs) {
                System.out.println("Retrieved run with ID " + Integer.toString(run.getID()));
                System.out.println("Run duration: " + Float.toString(run.getDuration()));
                System.out.println("Run distance: " + Float.toString(run.getDistance()));
                System.out.println("Run speed: " + Float.toString(run.getSpeed()));
                System.out.println("Run altitude ascended: " + run.getAltitudeAscended());
                System.out.println("Run altitude descended: " + run.getAltitudeDescended());
                System.out.println("Run date: " + run.getRunDate().toString());
                System.out.println();
            }
        }

        RunStats stats = new RunStats(runs);
        if (!stats.isEmpty()) {
            System.out.println("Average run speed: " + stats.getMeanSpeed());
            System.out.println("Average run duration: " + stats.getMeanDuration());
            System.out.println("Average run distance: " + stats.getMeanDistance());
            System.out.println("Total run distance: " + stats.getTotalDistance());
            System.out.println("Average altitude gained: " + stats.getMeanAltitudeAscended());
            System.out.println("Total altitude gained: " + stats.getTotalAltitudeAscended());
            System.out.println("Average altitude lost: " + stats.getMeanAltitudeDescended());
            System.out.println("Total altitude lost: " + stats.getTotalAltitudeDescended());
        }
        else {
            System.out.println("RunStats is empty. No stats to show.");
        }

        // Iteration 3 ends here

    }

}
