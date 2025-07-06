package com.restaurant.reservation.ui;

import com.restaurant.reservation.service.ReservationService;
import com.restaurant.reservation.service.TableService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Formular zum Anlegen einer Reservierung.
 */
public class ReservationFormFrame extends JFrame {
    private final ReservationService reservationService;
    private final DashboardFrame dashboard;

    private JComboBox<Integer> personsCombo;
    private JComboBox<String> dateCombo;
    private JComboBox<String> timeCombo;
    private JTextField nameField;
    private JLabel tableLabel;
    private Integer selectedTable;
    private final TableService tableService = new TableService();

    public ReservationFormFrame(DashboardFrame dashboard, ReservationService service) {
        this.dashboard = dashboard;
        this.reservationService = service;
        setTitle("Neue Reservierung");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10,10));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel formPanel = new JPanel(new GridLayout(6,2,5,5));

        formPanel.add(new JLabel("Personen:"));
        personsCombo = new JComboBox<>();
        for (int i=1;i<=10;i++) personsCombo.addItem(i);
        formPanel.add(personsCombo);

        formPanel.add(new JLabel("Datum:"));
        dateCombo = new JComboBox<>();
        for (int i=0;i<14;i++) {
            LocalDate d = LocalDate.now().plusDays(i);
            dateCombo.addItem(d.toString());
        }
        formPanel.add(dateCombo);

        formPanel.add(new JLabel("Uhrzeit:"));
        timeCombo = new JComboBox<>();
        LocalTime start = LocalTime.of(10,0);
        for (int i=0;i<=24;i++) {
            LocalTime t = start.plusMinutes(i*30L);
            if (t.isAfter(LocalTime.of(22,0))) break;
            timeCombo.addItem(t.format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        formPanel.add(timeCombo);

        formPanel.add(new JLabel("Gastname:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        JButton selectTableBtn = new JButton("Tisch auswählen");
        formPanel.add(selectTableBtn);

        formPanel.add(new JLabel("Gewählter Tisch:"));
        tableLabel = new JLabel("-");
        formPanel.add(tableLabel);

        JButton reserveButton = new JButton("Reservieren");
        JButton backButton = new JButton("Zurück");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(reserveButton);
        buttonPanel.add(backButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        selectTableBtn.addActionListener(e -> onSelectTable());
        backButton.addActionListener(e -> dispose());
        reserveButton.addActionListener(e -> onReserve());

        pack();
        setLocationRelativeTo(dashboard);
    }

    private void onSelectTable() {
        String date = (String) dateCombo.getSelectedItem();
        String time = (String) timeCombo.getSelectedItem();
        int persons = (Integer) personsCombo.getSelectedItem();
        TableSelectionDialog dialog = new TableSelectionDialog(this, reservationService, date, time, persons);
        dialog.setVisible(true);
        Integer tbl = dialog.getSelectedTable();
        if (tbl != null) {
            selectedTable = tbl;
            tableLabel.setText("Tisch "+tbl);
        }
    }

    private void onReserve() {
        String name = nameField.getText().trim();
        String date = (String) dateCombo.getSelectedItem();
        String time = (String) timeCombo.getSelectedItem();
        int persons = (Integer) personsCombo.getSelectedItem();
        if (name.isEmpty() || selectedTable == null) {
            JOptionPane.showMessageDialog(this, "Bitte Name und Tisch auswählen.");
            return;
        }
        try {
            com.restaurant.reservation.model.Table tbl = tableService.getTableById(selectedTable);
            if (tbl != null && persons > tbl.getSeats()) {
                JOptionPane.showMessageDialog(this, "Gewählter Tisch hat nicht genug Sitzplätze.");
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Konnte Sitzplatzanzahl nicht prüfen.", "Fehler", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            // format check
            LocalDate.parse(date);
            LocalTime.parse(time);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Ungültiges Datum oder Uhrzeit");
            return;
        }
        try {
            reservationService.addReservation(name, date, time, persons, selectedTable);
            dashboard.refreshTable();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }
}
