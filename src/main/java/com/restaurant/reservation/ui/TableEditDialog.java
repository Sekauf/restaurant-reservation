package com.restaurant.reservation.ui;

import com.restaurant.reservation.model.Table;
import com.restaurant.reservation.service.TableService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Dialogfenster zum Anlegen oder Bearbeiten eines Tischs.
 * Bietet Eingabefelder für Name, Sitzplätze und eine Checkbox für Projektor sowie Speichern/Abbrechen-Buttons.
 */
public class TableEditDialog extends JDialog {

    private boolean saved = false;  // Gibt an, ob der Benutzer die Änderungen gespeichert hat

    /**
     * Konstruktor für den Tisch-Dialog.
     * @param owner das Eltern-Fenster (Frame/Dialog), zu dem dieses Dialog modal ist
     * @param tableService Service für DB-Operationen auf Tischen
     * @param table der zu bearbeitende Tisch (oder null, wenn ein neuer Tisch angelegt werden soll)
     */
    public TableEditDialog(Window owner, TableService tableService, Table table) {
        super(owner, (table == null ? "Neuen Tisch anlegen" : "Tisch bearbeiten"), ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        // UI-Komponenten aufbauen
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        // Name-Feld
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel nameLabel = new JLabel("Bezeichnung:");
        JTextField nameField = new JTextField(20);
        if (table != null) {
            nameField.setText(table.getName());
        }
        namePanel.add(nameLabel);
        namePanel.add(nameField);
        formPanel.add(namePanel);

        // Sitzplätze-Feld (Spinner)
        JPanel seatsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel seatsLabel = new JLabel("Sitzplätze:");
        // Dynamisches Maximum: mind. 50 oder aktueller Wert, damit bestehende Werte nicht beschnitten werden
        int initialSeats = (table != null ? table.getSeats() : 1);
        int maxSeats = Math.max(50, initialSeats);
        JSpinner seatsSpinner = new JSpinner(new SpinnerNumberModel(initialSeats, 1, maxSeats, 1));
        seatsPanel.add(seatsLabel);
        seatsPanel.add(seatsSpinner);
        formPanel.add(seatsPanel);

        // Projektor-Checkbox
        JCheckBox projectorCheck = new JCheckBox("Projektor vorhanden");
        if (table != null && table.isHasProjector()) {
            projectorCheck.setSelected(true);
        }
        formPanel.add(projectorCheck);

        // Buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Speichern");
        JButton cancelButton = new JButton("Abbrechen");
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);

        // Panels zur Dialogoberfläche hinzufügen
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(formPanel, BorderLayout.CENTER);
        contentPane.add(buttonsPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);

        // Event-Handler für Speichern-Button
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            int seats = (Integer) seatsSpinner.getValue();
            boolean hasProj = projectorCheck.isSelected();
            // Validierung
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Bitte geben Sie eine Bezeichnung ein.", "Eingabefehler", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (seats <= 0) {
                JOptionPane.showMessageDialog(this, "Die Anzahl der Sitzplätze muss positiv sein.", "Eingabefehler", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                if (table == null) {
                    // neuen Tisch einfügen
                    Table newTable = new Table(name, seats, hasProj);
                    tableService.addTable(newTable);
                } else {
                    // bestehenden Tisch ändern
                    table.setName(name);
                    table.setSeats(seats);
                    table.setHasProjector(hasProj);
                    boolean ok = tableService.updateTable(table);
                    if (!ok) {
                        JOptionPane.showMessageDialog(this, "Der Tisch konnte nicht aktualisiert werden (evtl. nicht mehr vorhanden).", "Fehler", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                // Erfolgreich gespeichert
                saved = true;
                dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Fehler beim Speichern des Tischs.", "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Event-Handler für Abbrechen-Button
        cancelButton.addActionListener(e -> {
            // Schließen ohne zu speichern
            saved = false;
            dispose();
        });
    }

    /**
     * Liefert true, falls der Dialog mit "Speichern" beendet wurde.
     */
    public boolean isSaved() {
        return saved;
    }
}
