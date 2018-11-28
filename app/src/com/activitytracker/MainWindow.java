package com.activitytracker;

import mdlaf.animation.*;
import mdlaf.utils.MaterialColors;

import javax.swing.*;
import java.awt.Image;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class MainWindow {
    private JPanel m_rootPanel;
    private JPanel topPanel;
    private JButton buttonMyActivity;
    private JPanel contentPanel;
    private JLabel labelProfileIcon;
    private JPanel panelMyActivity;
    private JTable tableMyActivity;

    MainWindow() {
        setupUI();
        setupActionListeners();
    }

    private void setupUI() {

        // Apply Material-defined hover effect to buttons
        Color coolGrey10 = new Color(99, 102, 106);
        Color coolGrey11 = new Color(83, 86, 90);
        MaterialUIMovement.add(buttonMyActivity, coolGrey11);

        // Load and scale logo into UI
        String logoPath = "./assets/logo.png";
        ImageIcon imageIcon = new ImageIcon(getClass().getResource(logoPath));
        final Image image = imageIcon.getImage(); // transform it
        final Image newimg = image.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newimg);  // transform it back
        labelProfileIcon.setIcon(imageIcon);

        panelMyActivity.setVisible(true);
    }

    private void setupActionListeners() {
        // My Activity button
        buttonMyActivity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                panelMyActivity.setVisible(true);
            }
        });
    }

    JPanel rootPanel() {
        return m_rootPanel;
    }


}
