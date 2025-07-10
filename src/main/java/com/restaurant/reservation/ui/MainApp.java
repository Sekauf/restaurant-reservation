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
        // FlatLaf-Look-and-Feel
        java.util.Map<String, String> uiDefaults =
                java.util.Collections.singletonMap("@accentColor", "#ff5722");
        com.formdev.flatlaf.FlatLaf.setGlobalExtraDefaults(uiDefaults);
        FlatLightLaf.setup();
        UIManager.put("defaultFont", new FontUIResource("SansSerif", java.awt.Font.PLAIN, 14));

        // Datenbank vorbereiten
        ReservationDAO.createTable();
        TableDAO.createTable();
        ReservationDAO.mergeSampleDataFromFile("sql/sample_reservations.sql");
        ReservationDAO.importSampleDataIfEmpty();

        // Anwendung starten
        ReservationService service = new ReservationService();
        SwingUtilities.invokeLater(() -> {
            DashboardFrame frame = new DashboardFrame(service);
            frame.setVisible(true);
        });
    }
}
