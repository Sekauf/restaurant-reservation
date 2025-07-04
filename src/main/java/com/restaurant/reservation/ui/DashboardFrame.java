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
        JLayeredPane floorPlanPane = createFloorPlanPane();
        add(floorPlanPane, BorderLayout.CENTER);

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
    private JLayeredPane createFloorPlanPane() {
        JLayeredPane pane = new JLayeredPane();
        pane.setLayout(null);

        TableService tableService = new TableService();
        java.util.List<Table> tables;
        try {
            tables = tableService.getAllTables();
        } catch (java.sql.SQLException e) {
            tables = java.util.Collections.emptyList();
        }

        int x = 20, y = 20;
        for (Table t : tables) {
            JLabel lbl = createDraggableLabel("T" + t.getId());
            lbl.setBounds(x, y, 60, 40);
            pane.add(lbl);
            x += 70;
            if (x > 500) { x = 20; y += 50; }
        }

        return pane;
    }

    /** Erstellt ein verschiebbares Label für den Tischplan. */
    private JLabel createDraggableLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setBackground(new Color(222,184,135));
        lbl.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        java.awt.event.MouseAdapter ma = new java.awt.event.MouseAdapter() {
            Point offset;
            @Override public void mousePressed(java.awt.event.MouseEvent e) {
                offset = e.getPoint();
            }
            @Override public void mouseDragged(java.awt.event.MouseEvent e) {
                Point p = SwingUtilities.convertPoint(lbl, e.getPoint(), lbl.getParent());
                lbl.setLocation(p.x - offset.x, p.y - offset.y);
            }
        };
        lbl.addMouseListener(ma);
        lbl.addMouseMotionListener(ma);
        return lbl;
    }
}
