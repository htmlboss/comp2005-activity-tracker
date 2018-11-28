package com.activitytracker;

import mdlaf.animation.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Date;
import java.util.Vector;

class MainWindow {
    private final DefaultTableModel m_tableModel = new DefaultTableModel();

    private JPanel m_rootPanel;
    private JPanel topPanel;
    private JButton buttonImportData;
    private JPanel contentPanel;
    private JLabel labelProfileIcon;
    private JPanel panelMyActivity;
    private JTable tableMyActivity;

    private DBManager m_dbManager = null;
    private User m_user;


    MainWindow(DBManager dbmanager, final User user) {
        m_dbManager = dbmanager;
        m_user = user;

        setupUI();
        setupActionListeners();
    }

    private void setupUI() throws HeadlessException {

        // Apply Material-defined hover effect to buttons
        Color coolGrey11 = new Color(83, 86, 90);
        MaterialUIMovement.add(buttonImportData, coolGrey11);

        // Load and scale logo into UI
        String logoPath = "./assets/logo.png";
        ImageIcon imageIcon = new ImageIcon(getClass().getResource(logoPath));
        final Image image = imageIcon.getImage(); // transform it
        final Image newimg = image.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newimg);  // transform it back
        labelProfileIcon.setIcon(imageIcon);

        panelMyActivity.setVisible(true);

        // Populate table with all data
        populateTable();

        tableMyActivity.setModel(m_tableModel);
    }

    private void populateTable() {

        final Vector<String> columnNames = new Vector<>();
        columnNames.add("Date");
        columnNames.add("Duration");
        columnNames.add("Distance");
        columnNames.add("Altitude +");
        columnNames.add("Altitude -");

        final Vector<Integer> runIDs = m_dbManager.getRuns(m_user.getID(), new Date(Long.MIN_VALUE), new Date(Long.MAX_VALUE));
        final Vector<Vector<Object>> dataVector = new Vector<>();

        if (!runIDs.isEmpty()) {
            final Vector<Object> row = new Vector<>();

            row.add(1.0);
            row.add(2.0);
            row.add(3.0);
            row.add(4.0);
            row.add(5.0);

            dataVector.add(row);
        }

        m_tableModel.setDataVector(dataVector, columnNames);
        m_tableModel.setColumnCount(5);
    }

    private void setupActionListeners() {
        // My Activity button
        buttonImportData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                final JFileChooser fc = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
                fc.setFileFilter(filter);
                final int res = fc.showOpenDialog(null);

                if (res == JFileChooser.APPROVE_OPTION) {
                    final File file = fc.getSelectedFile();

                    new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() {

                            return null;
                        }
                    }.execute();
                }
            }
        });
    }

    JPanel rootPanel() {
        return m_rootPanel;
    }


}
