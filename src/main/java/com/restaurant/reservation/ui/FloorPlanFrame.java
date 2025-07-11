package com.restaurant.reservation.ui;

import com.restaurant.reservation.service.TableService;
import com.restaurant.reservation.service.ReservationService;
import com.restaurant.reservation.model.Table;
import com.restaurant.reservation.model.Reservation;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Darstellung eines grafischen Tischplans.
 */
public class FloorPlanFrame extends JFrame {

    private final TableService tableService = new TableService();
    private final ReservationService reservationService = new ReservationService();

    public FloorPlanFrame() {
        setTitle("Tischplan");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(0, 5, 10, 10));

        List<Table> tables;
        try {
            tables = tableService.getAllTables();
        } catch (SQLException e) {
            tables = java.util.Collections.emptyList();
        }

        for (Table t : tables) {
            JButton btn = new JButton("T" + t.getId());
            btn.addActionListener(e -> showTableInfo(t));
            panel.add(btn);
        }

        add(new JScrollPane(panel), BorderLayout.CENTER);
    }

    /** Zeigt ein Dialogfenster mit Informationen zum Tisch an. */
    private void showTableInfo(Table table) {
        StringBuilder sb = new StringBuilder();
        sb.append("Tisch ").append(table.getId()).append("\n");
        sb.append("Sitzplätze: ").append(table.getSeats()).append("\n\n");
        try {
            List<Reservation> list = reservationService.getReservationsForTable(table.getId());
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
        JOptionPane.showMessageDialog(this, sb.toString(), "Tisch " + table.getId(), JOptionPane.INFORMATION_MESSAGE);
    }
}
