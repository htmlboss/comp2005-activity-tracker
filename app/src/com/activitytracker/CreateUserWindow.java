package com.activitytracker;

import mdlaf.animation.MaterialUIMovement;
import mdlaf.utils.MaterialColors;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

public class CreateUserWindow extends JDialog {
    private JPanel m_rootPanel;
    private JTextField textFieldName;
    private JTextField textFieldEmail;
    private JPasswordField passwordField;
    private JButton buttonOk;
    private JTextField textFieldHeight;
    private JButton buttonCancel;
    private JTextField textFieldWeight;

    CreateUserWindow() {

        setupUI();
        setupActionListeners();
    }

    private void setupUI() {
        MaterialUIMovement.add(buttonCancel, MaterialColors.GRAY_100);
        MaterialUIMovement.add(buttonOk, MaterialColors.GRAY_100);
    }

    private void setupActionListeners() {
        buttonOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    JPanel rootPanel() {
        return m_rootPanel;
    }
}
