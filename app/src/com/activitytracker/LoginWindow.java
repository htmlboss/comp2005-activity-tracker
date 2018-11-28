package com.activitytracker;

import mdlaf.animation.*;
import mdlaf.utils.MaterialColors;

import javax.naming.AuthenticationException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.NoSuchElementException;

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

    private DBManager m_dbmanager = null;

    private java.util.function.Consumer<User> m_loginHandler;

    LoginWindow(java.util.function.Consumer<User> loginHandler, DBManager dbmanager) {
        m_loginHandler = loginHandler;
        m_dbmanager = dbmanager;

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
        m_createUserDialog.setContentPane(new CreateUserWindow(m_dbmanager, (Void) -> m_createUserDialog.hide() ).rootPanel());
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

                User user = null;
                try {
                    user = new User(m_dbmanager, textFieldUsername.getText(), String.valueOf(passwordField.getPassword()));
                } catch (final AuthenticationException ex) {
                    labelLoginMsg.setVisible(true);
                    labelLoginMsg.setText("Incorrect login.");
                    return;
                }
                catch (final NoSuchElementException ex) {
                    labelLoginMsg.setVisible(true);
                    labelLoginMsg.setText("User doesn't exist.");
                    return;
                }

                m_loginHandler.accept(user);

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
