package com.restaurant.reservation.ui;

import com.restaurant.reservation.model.Table;
import com.restaurant.reservation.service.TableService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

/**
 * Panel für die Verwaltung der Tische (Anzeige in einer Tabelle mit Bearbeitungsfunktionen).
 * Zeigt alle Tische in einem JTable an und bietet Schaltflächen zum Hinzufügen, Bearbeiten und Löschen.
 */
public class TableManagementPanel extends JPanel {

    private TableService tableService;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Table> tables;  // aktuell angezeigte Tische

    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;

    public TableManagementPanel() {
        this.tableService = new TableService();
        initializeComponents();
        loadTableData();
    }

    /** Initialisiert die UI-Komponenten und das Layout. */
    private void initializeComponents() {
        setLayout(new BorderLayout());
        // Tabelle einrichten
        tableModel = new DefaultTableModel(new Object[]{"ID", "Bezeichnung", "Sitzplätze", "Projektor"}, 0) {
            // Zellen nicht editierbar machen
            @Override public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons anlegen
        addButton = new JButton("Hinzufügen");
        editButton = new JButton("Bearbeiten");
        deleteButton = new JButton("Löschen");

        // Panel für Buttons am unteren Rand
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5)); // etwas Abstand
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event-Handler für Buttons registrieren
        addButton.addActionListener(e -> onAddTable());
        editButton.addActionListener(e -> onEditTable());
        deleteButton.addActionListener(e -> onDeleteTable());
    }

    /** Lädt die Tisch-Daten aus der Datenbank und zeigt sie in der Tabelle an. */
    private void loadTableData() {
        try {
            tables = tableService.getAllTables();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Fehler beim Laden der Tische.", "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Tabelle leeren
        tableModel.setRowCount(0);
        // Daten hinzufügen
        for (Table t : tables) {
            Object[] rowData = {
                    t.getId(),
                    t.getName(),
                    t.getSeats(),
                    (t.isHasProjector() ? "Ja" : "Nein")
            };
            tableModel.addRow(rowData);
        }
    }

    /** Aktion für Hinzufügen-Button: Öffnet den Bearbeitungsdialog für einen neuen Tisch. */
    private void onAddTable() {
        TableEditDialog dialog = new TableEditDialog(SwingUtilities.getWindowAncestor(this), tableService, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadTableData();
        }
    }

    /** Aktion für Bearbeiten-Button: Öffnet den Bearbeitungsdialog für den ausgewählten Tisch. */
    private void onEditTable() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Bitte wählen Sie einen Tisch aus.", "Kein Tisch ausgewählt", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Den ausgewählten Tisch ermitteln
        Table selectedTable = tables.get(selectedRow);
        TableEditDialog dialog = new TableEditDialog(SwingUtilities.getWindowAncestor(this), tableService, selectedTable);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadTableData();
        }
    }

    /** Aktion für Löschen-Button: Löscht den ausgewählten Tisch nach Bestätigung. */
    private void onDeleteTable() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Bitte wählen Sie einen Tisch aus.", "Kein Tisch ausgewählt", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Table selectedTable = tables.get(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Möchten Sie den Tisch \"" + selectedTable.getName() + "\" wirklich löschen?",
                "Löschen bestätigen",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            tableService.deleteTable(selectedTable.getId());
            loadTableData();
        } catch (IllegalStateException ex) {
            // Tisch wird verwendet
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Löschen nicht möglich", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Fehler beim Löschen des Tischs.", "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
        }
    }
}
