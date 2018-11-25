package com.activitytracker;
/**
 * \mainpage COMP-2005 Activity Tracker Documentation
 *
 * This website contains documentation for all source code contained in the <em>Activity Tracker</em> application.
 * Class and method documentation may be accessed in HTML format using the left-hand side navigation bar, or the
 * search box at the top right-hand side of the page.
 *
 * For offline viewing, a precompiled PDF of this documentation has been made available
 * <a href="https://htmlboss.github.io/comp2005-activity-tracker/tex/manual.pdf">here</a> Note, however, that this document does \em not contain the full source code
 * which is included in formatted HTML on this website.
 *
 * More detailed information about contributions, repository branches, and commit history is available by browsing
 * the <a href="https://github.com/htmlboss/comp2005-activity-tracker">GitHub repository</a> for this project.
 *
 */

import mdlaf.MaterialLookAndFeel;

import javax.swing.*;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

/**
 * The main program class.
 */
class ActivityTracker {

    /**
     * The main program entry point.
     */
    public static void main(final String[] args) {

        // Create singleton instance of DBManager
        DBManager dbManager = new DBManager();
        if (!dbManager.init("data.db")) {
            System.err.println("Failed to initialize DBManager");
            System.exit(1);
        }

        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(new MaterialLookAndFeel());
        }
        catch (final UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        // Get desktop resolution of default monitor (in case of multi-monitor setups)
        final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        // Create and populate the app window
        final JFrame frame = new JFrame("Activity Tracker");
        frame.setContentPane(new LoginWindow((Void) -> {
            frame.setContentPane(new MainWindow().rootPanel());
            frame.validate();
            frame.repaint();
        }).rootPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        // Set window size to be 1/2 of screen dimensions
        frame.setSize(gd.getDisplayMode().getWidth() / 2, gd.getDisplayMode().getHeight() / 2);
        frame.setLocationRelativeTo(null); // Center window
        frame.setVisible(true);
    }
}
