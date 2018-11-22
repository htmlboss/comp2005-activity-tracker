package com.activitytracker;

import mdlaf.MaterialLookAndFeel;

import javax.swing.*;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

class ActivityTracker {

    // Program entry point
    public static void main(final String[] args) {

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

        final JFrame frame = new JFrame("Activity Logger");

        final String logoPath = "./assets/logo.png";
        ImageIcon imgIcon = new ImageIcon(ActivityTracker.class.getResource(logoPath));
        frame.setIconImage(imgIcon.getImage());
        frame.setContentPane(new LoginWindow((Void) -> {
            frame.setContentPane(new MainWindow().rootPanel());
            frame.validate();
            frame.repaint();
        }).rootPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        // Set window size to be 2/3 of screen dimensions
        frame.setSize(gd.getDisplayMode().getWidth() * 2/3, gd.getDisplayMode().getHeight() * 2/3);
        frame.setLocationRelativeTo(null); // Center window
        frame.setVisible(true);
    }
}
