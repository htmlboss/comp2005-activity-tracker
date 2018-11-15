package com.activitytracker;

import mdlaf.animation.MaterialUIMovement;
import mdlaf.utils.MaterialColors;

import javax.swing.*;

public class CreateUserWindow {
    private JPanel m_rootPanel;
    private JTextField textFieldName;
    private JTextField textFieldEmail;
    private JPasswordField passwordField;
    private JButton okButton;
    private JTextField textFieldHeight;
    private JButton buttonCancel;

    CreateUserWindow() {
        setupUI();
    }

    private void setupUI() {
        MaterialUIMovement.add(buttonCancel, MaterialColors.GRAY_100);
        MaterialUIMovement.add(okButton, MaterialColors.GRAY_100);
    }

    JPanel rootPanel() {
        return m_rootPanel;
    }
}
