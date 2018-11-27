package com.activitytracker;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;

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


    /**
     * Adds a new workout to the database or updates an existing workout with new information that the user imported
     * from the log file.
     *
     * If (\em duration, \em distance, \em altitude) passed to this method is (0, 0, 0) then the intended assumption
     * is that this is the beginning of a new workout. As such, this input will cause a new row to be added to the
     * Runs table in the database and the user's last run ID attribute will be updated accordingly. If the input is
     * non-(0, 0, 0), then three things take place:
     *  -# The \em duration in the database is overwritten by the \em duration provided as input;
     *  -# The \em distance in the database is overwritten by the \em distance provided as input; and
     *  -# Existing values for \em altitude_ascended and \em altitude_descended are retrieved from the database,
     *     their difference is compared to the current relative altitude, and depending whether this difference is
     *     positive or negative, the appropriate field in the database is updated to reflect the change.
     *
     * @param dbManager Database connection with with the method interacts.
     * @param user A User object corresponding to the use whose run is being added to the database.
     * @param duration The length of time in seconds that the user's run lasted.
     * @param date The date the run occurred.
     * @param distance The cumulative distance (in metres) that the user ran as of the current time passed to the
     *                 method.
     * @param altitude The relative current altitude (in metres) of the user at the time point being entered.
     *                 Used to compute cumulative altitude ascended and descended throughout the run.
     */
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

    /**
     * Retrieves a set of runs from the database. Returns the result as a vector of Run objects.
     *
     * @param dbManager Database connection with with the method interacts.
     * @param user A User object corresponding to the use whose run(s) is/are being retrieved from the database.
     * @param startDate The beginning of the interval for which we are retrieving workouts.
     * @param endDate The end of the interval for which we are retrieving workouts.
     *
     * @return A vector containing instances of Run corresponding to all entered workouts between the start and end
     *         dates specified.
     */
    public static Vector<Run> getRuns(final DBManager dbManager, final User user, final Date startDate, final Date endDate) {
        Vector<Run> runs = null;

        return runs;
    }


    // Opens and iterates through a file

    /**
     * Opens and iterates through a file. The Run#newRunDataPoint() method is called for each line.
     *
     * @param dbManager Database connection with with the method interacts.
     * @param user A User object corresponding to the use whose run(s) is/are being retrieved from the database.
     * @param filePath The file to be iterated through
     *
     * @throws FileNotFoundException Thrown if the file path given does not exist.
     * @throws IOException Thrown if there is an error reading or opening the file.
     */
    public static void bulkImport(final DBManager dbManager, final User user, final String filePath) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line = null;  
        while ((line = br.readLine()) != null)  
        {
            String[] attributes = line.split(",");
            String buffTime = attributes[0];
            String buffDistance = attributes[1];
            String buffAltitude = attributes[2];
            String[] buffDate = attributes[3].split("-");
            String buffMonth = buffDate[1];
            String buffDay = buffDate[0];
            String buffYear = buffDate[2];
            Date runDate = new Date(Integer.parseInt(buffDay), Integer.parseInt(buffMonth), Integer.parseInt(buffYear));

            // Convert strings to floats
            // ToDo: String date to Date date
            float fDur = Float.parseFloat(buffTime);
            float fDist = Float.parseFloat(buffDistance);
            float fAlt = Float.parseFloat(buffAltitude);

            newRunDataPoint(dbManager, user, fDur, runDate, fDist, fAlt);
        } 
    }


}
