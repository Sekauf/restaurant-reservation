package com.restaurant.reservation.ui;

import com.restaurant.reservation.dao.ReservationDAO;
import com.restaurant.reservation.service.ReservationService;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Hauptklasse zum Starten der Restaurant-Reservierungsanwendung.
 * Initialisiert die Datenbank und öffnet die GUI.
 */
public class MainApp {
    public static void main(String[] args) {
        // Optional: System-Look-and-Feel verwenden für native GUI-Darstellung
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignorieren, falls nicht erfolgreich gesetzt
        }

        // Datenbank-Tabelle erstellen (falls noch nicht vorhanden)
        ReservationDAO.createTable();

        // Service und GUI starten
        ReservationService service = new ReservationService();
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(service);
            frame.setVisible(true);
        });
    }
}
