package com.activitytracker;

import mdlaf.animation.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private JLabel labelHeyThere;
    private JTextField textFieldEndDate;
    private JTextField textFieldStartDate;
    private JButton buttonGo;
    private JTextArea textAreaStats;

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
        MaterialUIMovement.add(buttonImportData, coolGrey11);

        // Load and scale logo into UI
        String logoPath = "./assets/logo.png";
        ImageIcon imageIcon = new ImageIcon(getClass().getResource(logoPath));
        final Image image = imageIcon.getImage(); // transform it
        final Image newimg = image.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newimg);  // transform it back
        labelProfileIcon.setIcon(imageIcon);

        textFieldStartDate.setText("DD-MM-YYYY");
        textFieldEndDate.setText("DD-MM-YYYY");

        // Show first name in greeting
        labelHeyThere.setText(String.format("Hey %s!", m_user.getName().split(" ", 2)[0]));

        panelMyActivity.setVisible(true);

        populateTable();

        tableMyActivity.setModel(m_tableModel);
    }

    private void populateTable() {
        Date start = new Date(Long.MIN_VALUE);
        Date end = new Date(Long.MAX_VALUE);
        populateTable(start, end);
    }

    private void populateTable(final Date start, final Date end) {

        // Clear table model
        m_tableModel.setRowCount(0);

        final Vector<String> columnNames = new Vector<>();
        columnNames.add("Date");
        columnNames.add("Duration (sec)");
        columnNames.add("Distance (m)");
        columnNames.add("Altitude + (m)");
        columnNames.add("Altitude - (m)");

        final Vector<Run> runs = Run.getRuns(m_dbManager, m_user, start, end);
        final Vector<Vector<Object>> dataVector = new Vector<>();

        if (runs == null) {
            return;
        }

        if (!runs.isEmpty()) {
            for (final Run run : runs) {
                final Vector<Object> row = new Vector<>();

                row.add(run.getRunDate());
                row.add(run.getDuration());
                row.add(run.getDistance());
                row.add(run.getAltitudeAscended());
                row.add(run.getAltitudeDescended());

                dataVector.add(row);
            }
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

                    // Run in separate thread
                    new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() {
                            buttonImportData.setEnabled(false);
                            try {
                                Run.bulkImport(m_dbManager, m_user, file.getAbsolutePath());
                            } catch(final Exception e) {
                                buttonImportData.setEnabled(true);
                                return null;
                            }

                            populateTable();

                            buttonImportData.setEnabled(true);
                            return null;
                        }
                    }.execute();
                }
            }
        });

        // Go button
        buttonGo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final String start = textFieldStartDate.getText();
                final String end = textFieldEndDate.getText();
                Date startDate, endDate;

                DateFormat sourceFormat = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    startDate = sourceFormat.parse(start);
                    endDate = sourceFormat.parse(end);
                    Vector<Run> runs = Run.getRuns(m_dbManager, m_user, startDate, endDate);
                    RunStats stats = new RunStats(runs);
                    String statStr = "Run Stats beginning at " + start + " and ending at " + end + ":\n" +
                            "Average Run Duration: " + stats.getMeanDuration() + "\n" +
                            "Average Speed: " + stats.getMeanSpeed() + "\n" +
                            "Average Distance: " + stats.getMeanDistance() + "\n" +
                            "Total Distance: " + stats.getTotalDistance() + "\n" +
                            "Average Altitude Ascended: " + stats.getMeanAltitudeAscended() + "\n" +
                            "Total Altiitude Ascended: " + stats.getTotalAltitudeAscended() + "\n" +
                            "Average Altitude Descended: " + stats.getMeanAltitudeDescended();
                    populateTable(startDate, endDate);
                    textAreaStats.setText(statStr);
                }
                catch (final ParseException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        });
    }

    JPanel rootPanel() {
        return m_rootPanel;
    }


}
