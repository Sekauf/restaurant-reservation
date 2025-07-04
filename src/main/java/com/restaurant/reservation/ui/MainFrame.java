package com.restaurant.reservation.ui;

import com.restaurant.reservation.model.Reservation;
import com.restaurant.reservation.service.ReservationService;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.JOptionPane;
import javax.swing.JComponent;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 * Hauptfenster der Anwendung mit Swing-Oberfläche.
 * Enthält die Tabelle der Reservierungen und das Formular zur Eingabe.
 */
public class MainFrame extends JFrame {
    private ReservationService service;
    private List<Reservation> reservations;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField nameField, dateField, timeField, personsField, tableNumberField;
    private JButton addButton, deleteButton;

    public MainFrame(ReservationService service) {
        this.service = service;
        setTitle("Restaurant-Reservierungssystem");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        // Randabstand um den Inhalt
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tabelle und Modell initialisieren
        String[] columnNames = { "ID", "Name", "Datum", "Uhrzeit", "Personen", "Tisch" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);

        // Formularfelder
        nameField = new JTextField();
        dateField = new JTextField();
        timeField = new JTextField();
        personsField = new JTextField();
        tableNumberField = new JTextField();
        // Hinweis-Texte (Tooltips) für Datum und Zeit
        dateField.setToolTipText("Format: YYYY-MM-DD");
        timeField.setToolTipText("Format: HH:MM (24h)");

        // Labels für die Formularfelder
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel dateLabel = new JLabel("Datum:");
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel timeLabel = new JLabel("Uhrzeit:");
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel personsLabel = new JLabel("Personen:");
        personsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel tableLabel = new JLabel("Tisch-Nr:");
        tableLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        // Panel für das Formular (5 Reihen, 2 Spalten)
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(dateLabel);
        formPanel.add(dateField);
        formPanel.add(timeLabel);
        formPanel.add(timeField);
        formPanel.add(personsLabel);
        formPanel.add(personsField);
        formPanel.add(tableLabel);
        formPanel.add(tableNumberField);

        // Buttons
        addButton = new JButton("Hinzufügen");
        deleteButton = new JButton("Löschen");
        // Panel für Buttons (vertikal untereinander)
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        // Unteres Panel: Formular links, Buttons rechts
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(formPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        // Zusammenbau des Hauptfensters
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Tabelle initial mit Daten befüllen
        refreshTable();

        // ActionListener für "Hinzufügen"
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String date = dateField.getText().trim();
            String time = timeField.getText().trim();
            String personsText = personsField.getText().trim();
            String tableText = tableNumberField.getText().trim();
            if (name.isEmpty() || date.isEmpty() || time.isEmpty() || personsText.isEmpty() || tableText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Bitte alle Felder ausfüllen.", "Eingabe fehlerhaft", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int persons;
            int tableNum;
            try {
                persons = Integer.parseInt(personsText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Bitte eine gültige Zahl für Personen eingeben.", "Eingabe fehlerhaft", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (persons <= 0) {
                JOptionPane.showMessageDialog(this, "Die Personenanzahl muss größer als 0 sein.", "Eingabe fehlerhaft", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                tableNum = Integer.parseInt(tableText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Bitte eine gültige Zahl für Tisch-Nr eingeben.", "Eingabe fehlerhaft", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (tableNum <= 0) {
                JOptionPane.showMessageDialog(this, "Die Tischnummer muss größer als 0 sein.", "Eingabe fehlerhaft", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Datum und Uhrzeit auf korrektes Format prüfen
            LocalDate dateObj;
            LocalTime timeObj;
            try {
                dateObj = LocalDate.parse(date);
                timeObj = LocalTime.parse(time);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Bitte Datum (YYYY-MM-DD) und Zeit (HH:MM) im gültigen Format eingeben.", "Eingabe fehlerhaft", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Strings normalisieren (führende Nullen etc.)
            date = dateObj.format(DateTimeFormatter.ISO_LOCAL_DATE);
            time = timeObj.format(DateTimeFormatter.ofPattern("HH:mm"));
            // Reservierung hinzufügen (mit Service)
            try {
                service.addReservation(name, date, time, persons, tableNum);
                refreshTable();
                // Eingabefelder leeren und Fokus zurück auf Name
                nameField.setText("");
                dateField.setText("");
                timeField.setText("");
                personsField.setText("");
                tableNumberField.setText("");
                nameField.requestFocus();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ActionListener für "Löschen"
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Bitte wählen Sie eine Reservierung aus der Liste aus.", "Keine Auswahl", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Bestätigung vor dem Löschen einholen
            int confirm = JOptionPane.showConfirmDialog(this, "Ausgewählte Reservierung löschen?", "Löschen bestätigen", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
            // ID der ausgewählten Reservierung ermitteln
            Reservation selectedRes = reservations.get(selectedRow);
            int id = selectedRes.getId();
            try {
                service.deleteReservation(id);
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Fenstergröße setzen und zentrieren
        setSize(800, 500);
        setLocationRelativeTo(null);
    }

    /**
     * Lädt die aktuellen Reservierungen aus der Datenbank und aktualisiert die Tabelle.
     */
    private void refreshTable() {
        try {
            reservations = service.getAllReservations();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Fehler beim Laden der Reservierungen: " + e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            reservations = new ArrayList<>();
        }
        // Tabellendaten löschen
        tableModel.setRowCount(0);
        // Jede Reservierung als neue Zeile hinzufügen
        for (Reservation res : reservations) {
            Object[] rowData = {
                    res.getId(),
                    res.getName(),
                    res.getDate(),
                    res.getTime(),
                    res.getPersons(),
                    res.getTableNumber()
            };
            tableModel.addRow(rowData);
        }
    }
}
