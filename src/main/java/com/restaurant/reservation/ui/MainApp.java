package com.restaurant.reservation.ui;

import com.restaurant.reservation.dao.ReservationDAO;
import com.restaurant.reservation.dao.TableDAO;
import com.restaurant.reservation.service.ReservationService;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Hauptklasse zum Starten der Restaurant-Reservierungsanwendung.
 * Initialisiert die Datenbank und öffnet die GUI.
 */
public class MainApp {
    public static void main(String[] args) {
        // System-Look-and-Feel für moderne Darstellung
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignorieren, falls nicht verfügbar
        }
        // Einfache Braun-Beige-Farbgebung
        java.awt.Color primary = new java.awt.Color(143, 96, 60);
        java.awt.Color secondary = new java.awt.Color(224, 207, 181);
        java.awt.Color accent = new java.awt.Color(193, 149, 108);
        java.awt.Color darkRed = new java.awt.Color(139, 0, 0);
        UIManager.put("control", darkRed);
        UIManager.put("Button.background", primary);
        UIManager.put("Button.foreground", java.awt.Color.BLACK);
        UIManager.put("Table.background", secondary);
        UIManager.put("Table.foreground", java.awt.Color.BLACK);

        // Datenbank-Tabellen erstellen (falls noch nicht vorhanden)
        ReservationDAO.createTable();
        TableDAO.createTable();

        // Service und GUI starten
        ReservationService service = new ReservationService();
        SwingUtilities.invokeLater(() -> {
            DashboardFrame frame = new DashboardFrame(service);
            frame.setVisible(true);
        });
    }
}
