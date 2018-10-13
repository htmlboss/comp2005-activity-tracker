package com.activitytracker;

import mdlaf.*;
import mdlaf.animation.*;
import mdlaf.utils.MaterialColors;

import javax.swing.*;
import java.awt.*;

public class MainWindow {
    private JPanel m_rootPanel;
    private JButton button1;
    private JProgressBar progressBar1;
    private JTabbedPane tabbedPane1;


    public MainWindow() {

        MaterialUIMovement.add(button1, MaterialColors.GRAY_100);
    }

    public static void main(final String[] args) {
        try {
            UIManager.setLookAndFeel(new MaterialLookAndFeel ());
        }
        catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace ();
        }

        // Get desktop resolution of default monitor (in case of multi-monitor setups)
        final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        final JFrame frame = new JFrame("Activity Tracker");
        frame.setContentPane(new MainWindow().m_rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        // Set window size to be 1/2 of screen dimensions
        frame.setSize(gd.getDisplayMode().getWidth() / 2, gd.getDisplayMode().getHeight() / 2);
        frame.setLocationRelativeTo(null); // Center window
        frame.setVisible(true);
    }
}
