package com.restaurant.reservation.ui;

import com.restaurant.reservation.dao.ReservationDAO;
import com.restaurant.reservation.dao.TableDAO;
import com.restaurant.reservation.service.ReservationService;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import com.formdev.flatlaf.FlatLightLaf;

/**
 * Hauptklasse zum Starten der Restaurant-Reservierungsanwendung.
 * Initialisiert die Datenbank und öffnet die GUI.
 */
public class MainApp {
    public static void main(String[] args) {
        // Modernes FlatLaf-Look-and-Feel aktivieren
        FlatLightLaf.setup();
        // Lesbarere Standardschrift nutzen
        UIManager.put("defaultFont", new FontUIResource("SansSerif", java.awt.Font.PLAIN, 14));

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
