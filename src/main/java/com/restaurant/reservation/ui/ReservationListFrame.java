package com.restaurant.reservation.ui;

import com.restaurant.reservation.model.Reservation;
import com.restaurant.reservation.service.ReservationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Anzeige aller Reservierungen.
 */
public class ReservationListFrame extends JFrame {
    private final ReservationService service;
    private JTable table;
    private DefaultTableModel model;
    private List<Reservation> reservations = new ArrayList<>();

    public ReservationListFrame(ReservationService service) {
        this.service = service;
        setTitle("Alle Reservierungen");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10,10));
        ((JComponent)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        model = new DefaultTableModel(new Object[]{"Datum","Uhrzeit","Tisch","Gast"},0) {
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton deleteBtn = new JButton("Stornieren");
        add(deleteBtn, BorderLayout.SOUTH);

        deleteBtn.addActionListener(e -> onDelete());

        setSize(600,400);
        setLocationRelativeTo(null);
        loadData();
    }

    private void loadData() {
        try {
            reservations = service.getAllReservations();
            model.setRowCount(0);
            for (Reservation r : reservations) {
                model.addRow(new Object[]{r.getDate(), r.getTime(), r.getTableNumber(), r.getName()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Reservierungen konnten nicht geladen werden.");
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Bitte eine Reservierung auswählen.");
            return;
        }
        Reservation res = reservations.get(row);
        int confirm = JOptionPane.showConfirmDialog(this, "Reservierung stornieren?", "Bestätigen", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            service.deleteReservation(res.getId());
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }
}
