package com.restaurant.reservation.ui;

import com.restaurant.reservation.model.Reservation;
import com.restaurant.reservation.service.ReservationService;
import com.restaurant.reservation.ui.FloorPlanFrame;
import com.restaurant.reservation.ui.StatisticsFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Hauptfenster (Dashboard) mit heutigen Reservierungen und Navigation.
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

        JLabel headline = new JLabel("Heutige Reservierungen");
        headline.setFont(headline.getFont().deriveFont(Font.BOLD, 18f));
        add(headline, BorderLayout.NORTH);

        String[] cols = {"Uhrzeit","Personen","Tisch","Gast"};
        tableModel = new DefaultTableModel(cols,0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        reservationTable = new JTable(tableModel);
        add(new JScrollPane(reservationTable), BorderLayout.CENTER);

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

    /** Reload today's reservations */
    public void refreshTable() {
        try {
            List<Reservation> all = reservationService.getAllReservations();
            reservations.clear();
            tableModel.setRowCount(0);
            for (Reservation r : all) {
                if (LocalDate.parse(r.getDate()).isEqual(LocalDate.now())) {
                    reservations.add(r);
                    tableModel.addRow(new Object[]{r.getTime(), r.getPersons(), r.getTableNumber(), r.getName()});
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Reservierungen konnten nicht geladen werden.", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }
}
