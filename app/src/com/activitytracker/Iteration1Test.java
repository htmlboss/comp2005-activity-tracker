package com.activitytracker;

import javax.naming.AuthenticationException;
import java.util.Date;

public class Iteration1Test {

    public static void main(String[] args) {

        User john;

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



        System.out.println("Authenticating user...");

        try {
            john = new User(dbManager,"jdoe@mac.com", "My Very Secure Password");
        }
        catch (final AuthenticationException e) {
            System.out.println("User could not be authenticated.");
        }

        System.out.println("Testing incorrect password...");

        try {
            john = new User(dbManager,"jdoe@mac.com", "Some Incorrect Password");
        }
        catch (final AuthenticationException e) {
            System.out.println("Incorrect password used; authentication failed.");
        }


    }

}
