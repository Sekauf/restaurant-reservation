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
        // Modernes FlatLaf-Look-and-Feel aktivieren und mit
        // einer eigenen Akzentfarbe versehen, damit die
        // Oberfläche etwas farbiger wirkt
        java.util.Map<String, String> uiDefaults =
                java.util.Collections.singletonMap("@accentColor", "#ff5722");
        com.formdev.flatlaf.FlatLaf.setGlobalExtraDefaults(uiDefaults);
        FlatLightLaf.setup();
        // Lesbarere Standardschrift nutzen
        UIManager.put("defaultFont", new FontUIResource("SansSerif", java.awt.Font.PLAIN, 14));

        // Datenbank-Tabellen erstellen (falls noch nicht vorhanden)
        ReservationDAO.createTable();
        TableDAO.createTable();
        // Beispielreservierungen importieren, falls die Tabelle leer ist
        ReservationDAO.importSampleDataIfEmpty();

        // Service und GUI starten
        ReservationService service = new ReservationService();
        SwingUtilities.invokeLater(() -> {
            DashboardFrame frame = new DashboardFrame(service);
            frame.setVisible(true);
        });
    }
}
