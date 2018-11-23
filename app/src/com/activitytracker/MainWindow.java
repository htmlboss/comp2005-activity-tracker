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
    private JButton buttonAddDevice;
    private JButton buttonMyFriends;
    private JPanel contentPanel;
    private JLabel labelProfileIcon;
    private JPanel panelMyActivity;
    private JPanel panelAddDevice;
    private JPanel panelMyFriends;
    private JScrollPane scrollPaneMyFriends;
    private JTable tableAvailableDevices;
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
        MaterialUIMovement.add(buttonAddDevice, coolGrey11);
        MaterialUIMovement.add(buttonMyFriends, coolGrey11);

        // Load and scale logo into UI
        String logoPath = "./assets/logo.png";
        ImageIcon imageIcon = new ImageIcon(getClass().getResource(logoPath));
        final Image image = imageIcon.getImage(); // transform it
        final Image newimg = image.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newimg);  // transform it back
        labelProfileIcon.setIcon(imageIcon);

        panelMyActivity.setVisible(true);
        panelAddDevice.setVisible(false);
        panelMyFriends.setVisible(false);
    }

    private void setupActionListeners() {
        // My Activity button
        buttonMyActivity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                panelMyActivity.setVisible(true);
                panelAddDevice.setVisible(false);
                panelMyFriends.setVisible(false);
            }
        });
        // Add Device button
        buttonAddDevice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                panelMyActivity.setVisible(false);
                panelAddDevice.setVisible(true);
                panelMyFriends.setVisible(false);
            }
        });
        // My Friends button
        buttonMyFriends.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                panelMyActivity.setVisible(false);
                panelAddDevice.setVisible(false);
                panelMyFriends.setVisible(true);
            }
        });
    }

    JPanel rootPanel() {
        return m_rootPanel;
    }


}
