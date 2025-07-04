package com.restaurant.reservation.ui;

import com.restaurant.reservation.model.Reservation;
import com.restaurant.reservation.service.ReservationService;
import com.restaurant.reservation.service.TableService;
import com.restaurant.reservation.model.Table;
import com.restaurant.reservation.ui.FloorPlanFrame;
import com.restaurant.reservation.ui.StatisticsFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Hauptfenster (Dashboard) mit Tischplan und 
 * einer kleinen Liste der kommenden Reservierungen.
 */
public class DashboardFrame extends JFrame {
    private final ReservationService reservationService;
    private JTable reservationTable;
    private DefaultTableModel tableModel;
    private List<Reservation> reservations = new ArrayList<>();

    public DashboardFrame(ReservationService service) {
        this.reservationService = service;
        setTitle("La Bella Trattoria");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // Panel mit Tischplan in der Mitte
        JPanel floorPlanPane = createFloorPlanPane();
        add(new JScrollPane(floorPlanPane), BorderLayout.CENTER);

        // Panel für die nächsten Reservierungen in der rechten Ecke
        String[] cols = {"Datum","Uhrzeit","Tisch","Gast"};
        tableModel = new DefaultTableModel(cols,0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        reservationTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(reservationTable);
        tableScroll.setPreferredSize(new Dimension(250, 0));
        JPanel reservationPanel = new JPanel(new BorderLayout());
        JLabel headline = new JLabel("Nächste Reservierungen");
        headline.setFont(headline.getFont().deriveFont(Font.BOLD, 16f));
        reservationPanel.add(headline, BorderLayout.NORTH);
        reservationPanel.add(tableScroll, BorderLayout.CENTER);
        add(reservationPanel, BorderLayout.EAST);

        JButton newResButton = new JButton("Neue Reservierung");
        JButton allResButton = new JButton("Alle Reservierungen");
        JButton planButton = new JButton("Tischplan");
        JButton statsButton = new JButton("Statistiken");
        newResButton.setFont(newResButton.getFont().deriveFont(Font.BOLD, 14f));
        allResButton.setFont(allResButton.getFont().deriveFont(Font.BOLD, 14f));
        planButton.setFont(planButton.getFont().deriveFont(Font.BOLD, 14f));
        statsButton.setFont(statsButton.getFont().deriveFont(Font.BOLD, 14f));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(newResButton);
        buttonPanel.add(allResButton);
        buttonPanel.add(planButton);
        buttonPanel.add(statsButton);
        add(buttonPanel, BorderLayout.SOUTH);

        newResButton.addActionListener(e -> {
            ReservationFormFrame form = new ReservationFormFrame(this, reservationService);
            form.setVisible(true);
            refreshTable();
        });

        allResButton.addActionListener(e -> {
            ReservationListFrame list = new ReservationListFrame(reservationService);
            list.setVisible(true);
        });

        planButton.addActionListener(e -> {
            FloorPlanFrame plan = new FloorPlanFrame();
            plan.setVisible(true);
        });

        statsButton.addActionListener(e -> {
            StatisticsFrame stats = new StatisticsFrame(reservationService);
            stats.setVisible(true);
        });

        setSize(700,400);
        setLocationRelativeTo(null);
        refreshTable();
    }

    /**
     * Lädt die kommenden Reservierungen (heute bis 7 Tage im Voraus)
     * und aktualisiert die Tabelle im Dashboard.
     */
    public void refreshTable() {
        try {
            List<Reservation> all = reservationService.getAllReservations();
            reservations.clear();
            tableModel.setRowCount(0);
            LocalDate today = LocalDate.now();
            LocalDate limit = today.plusDays(7);
            for (Reservation r : all) {
                LocalDate date = LocalDate.parse(r.getDate());
                if (!date.isBefore(today) && !date.isAfter(limit)) {
                    reservations.add(r);
                    tableModel.addRow(new Object[]{r.getDate(), r.getTime(), r.getTableNumber(), r.getName()});
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Reservierungen konnten nicht geladen werden.", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Erstellt das Panel mit dem Tischplan. */
    private JPanel createFloorPlanPane() {
        JPanel panel = new JPanel(new GridLayout(0, 5, 10, 10));

        TableService tableService = new TableService();
        java.util.List<Table> tables;
        try {
            tables = tableService.getAllTables();
        } catch (java.sql.SQLException e) {
            tables = java.util.Collections.emptyList();
        }

        for (Table t : tables) {
            JButton btn = new JButton("T" + t.getId());
            btn.addActionListener(e -> showTableInfo(t));
            panel.add(btn);
        }

        return panel;
    }

    /** Zeigt ein Dialogfenster mit Informationen zum Tisch an. */
    private void showTableInfo(Table table) {
        StringBuilder sb = new StringBuilder();
        sb.append("Tisch ").append(table.getId()).append("\n");
        sb.append("Sitzplätze: ").append(table.getSeats()).append("\n\n");
        try {
            java.util.List<Reservation> list = reservationService.getReservationsForTable(table.getId());
            if (list.isEmpty()) {
                sb.append("Keine Reservierungen vorhanden");
            } else {
                sb.append("Reservierungen:\n");
                for (Reservation r : list) {
                    sb.append(r.getDate()).append(" ").append(r.getTime())
                      .append(" - ").append(r.getName()).append("\n");
                }
            }
        } catch (Exception ex) {
            sb.append("Reservierungen konnten nicht geladen werden.");
        }
        JOptionPane.showMessageDialog(this, sb.toString(),
                "Tisch " + table.getId(), JOptionPane.INFORMATION_MESSAGE);
    }
}
