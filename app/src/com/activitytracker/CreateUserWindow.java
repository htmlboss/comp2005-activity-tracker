package com.activitytracker;

import mdlaf.animation.MaterialUIMovement;
import mdlaf.utils.MaterialColors;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateUserWindow extends JDialog {
    private JPanel m_rootPanel;
    private JTextField textFieldName;
    private JTextField textFieldEmail;
    private JPasswordField passwordField;
    private JButton buttonOk;
    private JTextField textFieldHeight;
    private JButton buttonCancel;
    private JTextField textFieldWeight;

    DBManager m_dbmanager = null;

    CreateUserWindow(DBManager dbmanager) {
        m_dbmanager = dbmanager;

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

                if (textFieldName.getText().isEmpty() ||
                    textFieldEmail.getText().isEmpty() ||
                    passwordField.getPassword().length == 0) {

                    return;
                }
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
