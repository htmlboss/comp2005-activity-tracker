package com.activitytracker;

import mdlaf.animation.MaterialUIMovement;
import mdlaf.utils.MaterialColors;
import org.jfree.ui.IntegerDocument;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

public class CreateUserWindow extends JDialog {
    private JPanel m_rootPanel;
    private JTextField textFieldName;
    private JTextField textFieldEmail;
    private JPasswordField passwordField;
    private JButton buttonOk;
    private JTextField textFieldHeight;
    private JButton buttonCancel;
    private JTextField textFieldWeight;
    private JComboBox comboBoxBirthMonth;
    private JComboBox comboBoxBirthDay;
    private JTextField textFieldBirthYear;
    private JComboBox comboBoxSex;
    private JLabel labelInfo;

    private DBManager m_dbmanager = null;

    private java.util.function.Consumer<Void> m_closeWindowHandler;

    CreateUserWindow(DBManager dbmanager, java.util.function.Consumer<Void> closeWindowHandler) {
        m_dbmanager = dbmanager;
        m_closeWindowHandler = closeWindowHandler;

        setupUI();
        setupActionListeners();
    }

    private void setupUI() {
        MaterialUIMovement.add(buttonCancel, MaterialColors.GRAY_100);
        MaterialUIMovement.add(buttonOk, MaterialColors.GRAY_100);

        labelInfo.setVisible(false);
    }

    private void setupActionListeners() {
        buttonOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (textFieldName.getText().isEmpty() ||
                        textFieldEmail.getText().isEmpty() ||
                        passwordField.getPassword().length == 0 ||
                        textFieldHeight.getText().isEmpty() ||
                        textFieldWeight.getText().isEmpty() ||
                        textFieldBirthYear.getText().isEmpty()) {

                    m_closeWindowHandler.accept(null);
                    return;
                }

                try {
                    User.createUser(m_dbmanager,
                            textFieldName.getText(),
                            textFieldEmail.getText(),
                            new Date(Integer.parseInt(textFieldBirthYear.getText()), comboBoxBirthMonth.getSelectedIndex() + 1, comboBoxBirthDay.getSelectedIndex() + 1),
                            (comboBoxSex.getSelectedIndex() == 0 ? User.Sex.MALE : User.Sex.FEMALE),
                            Float.parseFloat(textFieldHeight.getText()),
                            Float.parseFloat(textFieldWeight.getText()),
                            String.valueOf(passwordField.getPassword()));
                } catch(final AssertionError ex) {
                    labelInfo.setVisible(true);
                    labelInfo.setText("User already exists.");
                }

                m_closeWindowHandler.accept(null);
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                m_closeWindowHandler.accept(null);
            }
        });
    }

    JPanel rootPanel() {
        return m_rootPanel;
    }
}
