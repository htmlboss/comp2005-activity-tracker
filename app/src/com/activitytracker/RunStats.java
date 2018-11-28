package com.activitytracker;

import java.util.Vector;

/**
 * The RunStats class is used to compute statistics for a selection of past runs.
 *
 * It is intended to be integrated with the Run#getRuns method, as that returns a vector of Run
 * objects that match search parameters, and the constructor of this class accepts a vector of
 * Run objects, or nothing.
 */
public class RunStats {
    /**
     * The runs for which the statistics in the RunStats object pertain.
     */
    Vector<Run> runs;
    /**
     * The average speed for all runs in RunStats#runs.
     */
    float meanSpeed;
    /**
     * Cumulative distance ran for all runs in RunStats#runs.
     */
    float totalDistance;
    /**
     * Mean distance per run, computed using the runs in RunStats#runs.
     */
    float meanDistance;
    /**
     * Cumulative altitude climbed for all runs in RunStats#runs.
     */
    float totalAltitudeAscended;
    /**
     * Mean altitude climbed per run, computed using the runs in RunStats#runs.
     */
    float meanAltitudeAscended;
    /**
     * Cumulative altitude descended for all runs in RunStats#runs.
     */
    float totalAltitudeDescended;
    /**
     * Mean altitude descended per run, computed using the runs in RunStats#runs.
     */
    float meanAltitudeDescended;
    /**
     * Mean duration per run, computed using the runs in RunStats#runs.
     */
    float meanDuration;

    /**
     * Stores the \em runs parameter in RunStats#runs and computes all statistics based on the
     * contents of this vector.
     *
     * @param runs A vector of Run objects with which we compute statistics.
     */
    RunStats(final Vector<Run> runs) {
        this.runs = runs;
        this.computeAll();
    }

    /**
     * An overloaded constructor which takes no argument. Here we assign to RunStats#runs an empty
     * vector. Computations are attempted but since the vector has size of zero, RunStats#compute()
     * assigns all stats the value \em 0.0f.
     *
     * You may want to use RunStats#addRun() to add Runs to a (possibly empty) RunStats object.
     */
    RunStats() {
        this(new Vector<>());
    }

    /**
     * A wrapper for the compute() method in this class that invokes all calculations.
     *
     * Basically, it's the lazy man's way of calling the full computation.
     */
    private void computeAll() {
        this.compute(RunAttribute.SPEED);
        this.compute(RunAttribute.DISTANCE);
        this.compute(RunAttribute.ALTITUDE_ASCENDED);
        this.compute(RunAttribute.ALTITUDE_DESCENDED);
        this.compute(RunAttribute.DURATION);
    }

    /**
     * Computes and stores averages for the RunAttribute passed as \em attribute, and stores
     * a total sum where applicable.
     *
     * @param attribute The RunAttribute for which we are computing statistics.
     */
    private void compute(final RunAttribute attribute) {
        float sum = 0.0f;
        int numRuns = runs.size();

        // If there are no runs, set everything to 0
        if (numRuns == 0) {
            this.meanSpeed = 0.0f;
            this.totalDistance = 0.0f;
            this.meanDistance = 0.0f;
            this.totalAltitudeAscended = 0.0f;
            this.meanAltitudeAscended = 0.0f;
            this.totalAltitudeDescended = 0.0f;
            this.meanAltitudeDescended = 0.0f;
            this.meanDuration = 0.0f;
            return;
        }

        switch (attribute) {
            case DISTANCE:
                for (Run run : this.runs)
                    sum += run.getDistance();
                this.totalDistance = sum;
                break;
            case DURATION:
                for (Run run : this.runs)
                    sum += run.getDuration();
                break;
            case ALTITUDE_ASCENDED:
                for (Run run : this.runs)
                    sum += run.getAltitudeAscended();
                this.totalAltitudeAscended = sum;
                break;
            case ALTITUDE_DESCENDED:
                for (Run run : this.runs)
                    sum += run.getAltitudeDescended();
                this.totalAltitudeDescended = sum;
                break;
            case SPEED:
                for (Run run : this.runs)
                    sum += run.getSpeed();
                break;
        }

        float mean = sum / numRuns;

        switch (attribute) {
            case DISTANCE:
                this.meanDistance = mean;
                break;
            case DURATION:
                this.meanDuration = mean;
                break;
            case ALTITUDE_ASCENDED:
                this.meanAltitudeAscended = mean;
                break;
            case ALTITUDE_DESCENDED:
                this.meanAltitudeDescended = mean;
                break;
            case SPEED:
                this.meanSpeed = mean;
        }
    }

    /**
     * Adds a Run object to the RunStats#runs vector and re-computes statistics by invoking
     * RunStats#computeAll().
     *
     * @param run A Run object to be appended to the set of runs that the RunStats statistics
     *            are based on.
     */
    public void addRun(Run run) {
        this.runs.addElement(run);
        this.computeAll();
    }

    /**
     * Checks if an instance of RunStats has any runs with with statistics have been computed.
     *
     * If this method returns \em False then all statistics are set to \em 0.0f (\em i.e.,
     * they are useless).
     *
     * @return True if the RunStats#runs vector has a size of zero, false otherwise.
     */
    public boolean isEmpty() {
        return this.runs.size() == 0;
    }

    public float getMeanSpeed() {
        return meanSpeed;
    }

    public float getTotalDistance() {
        return totalDistance;
    }

    public float getMeanDistance() {
        return meanDistance;
    }

    public float getTotalAltitudeAscended() {
        return totalAltitudeAscended;
    }

    public float getMeanAltitudeAscended() {
        return meanAltitudeAscended;
    }

    public float getTotalAltitudeDescended() {
        return totalAltitudeDescended;
    }

    public float getMeanAltitudeDescended() {
        return meanAltitudeDescended;
    }

    public float getMeanDuration() {
        return meanDuration;
    }
}
