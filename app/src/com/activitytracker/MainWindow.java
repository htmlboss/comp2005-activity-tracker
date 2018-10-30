package com.activitytracker;

import mdlaf.animation.*;
import mdlaf.utils.MaterialColors;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class MainWindow {
    private JPanel m_rootPanel;
    private JPanel topPanel;
    private JButton buttonMyActivity;
    private JButton buttonAddDevice;
    private JButton buttonMyFriends;
    private JToolBar toolBar;
    private JPanel contentPanel;
    private JLabel toolBarLabel;
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
        MaterialUIMovement.add(buttonMyActivity, MaterialColors.GRAY_100);
        MaterialUIMovement.add(buttonAddDevice, MaterialColors.GRAY_100);
        MaterialUIMovement.add(buttonMyFriends, MaterialColors.GRAY_100);

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

    public JPanel rootPanel() {
        return m_rootPanel;
    }


}
