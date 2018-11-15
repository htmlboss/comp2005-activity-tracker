package com.activitytracker;

import mdlaf.animation.*;
import mdlaf.utils.MaterialColors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginWindow extends JFrame {
    private JLabel labelTitle;
    private JTextField textFieldUsername;
    private JPasswordField passwordField;
    private JLabel labelUsername;
    private JLabel labelPassword;
    private JButton buttonLogin;
    private JLabel labelLoginMsg;
    private JPanel m_rootPanel;
    private JButton buttonCreateUser;

    private JDialog m_createUserDialog = null;

    private java.util.function.Consumer<Void> m_loginHandler;

    LoginWindow(java.util.function.Consumer<Void> loginHandler) {
        m_loginHandler = loginHandler;

        setupUI();
        setupCreateUserDialog();
        setupActionListeners();
    }

    private void setupUI() {
        MaterialUIMovement.add(buttonLogin, MaterialColors.GRAY_100);
        MaterialUIMovement.add(buttonCreateUser, MaterialColors.GRAY_100);

        labelLoginMsg.setVisible(false);
    }

    private void setupCreateUserDialog() {

        // Get desktop resolution of default monitor (in case of multi-monitor setups)
        final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        m_createUserDialog = new JDialog(this, "Activity Logger | Create User", true);
        m_createUserDialog.setContentPane(new CreateUserWindow().rootPanel());
        m_createUserDialog.pack();
        // Set window size to be 1/2 of screen dimensions
        m_createUserDialog.setSize(gd.getDisplayMode().getWidth() / 2, gd.getDisplayMode().getHeight() / 2);
        m_createUserDialog.setLocationRelativeTo(this); // Center window
    }

    private void setupActionListeners() {

        // Login button
        buttonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Do nothing if login fields are empty
                if (textFieldUsername.getText().isEmpty() || passwordField.getPassword().length == 0) {
                    return;
                }

                // Change to verifyLogin()
                if (true) {
                    m_loginHandler.accept(null);
                    return;
                }

                // Display error message
            }
        });

        // Create user button
        buttonCreateUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                m_createUserDialog.setVisible(true);
            }
        });
    }

    JPanel rootPanel() {
        return m_rootPanel;
    }

}
