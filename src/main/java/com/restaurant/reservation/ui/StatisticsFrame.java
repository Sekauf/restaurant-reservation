package com.restaurant.reservation.ui;

import com.restaurant.reservation.service.ReservationService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Einfaches Fenster zur Anzeige einiger KPIs und Statistiken.
 */
public class StatisticsFrame extends JFrame {
    private final ReservationService service;

    public StatisticsFrame(ReservationService service) {
        this.service = service;
        setTitle("Statistiken");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buildUi();
        setSize(400,300);
        setLocationRelativeTo(null);
    }

    private void buildUi() {
        // Panel mit etwas Rand, damit die Labels nicht direkt am Rand kleben
        JPanel content = new JPanel(new GridLayout(0,1,5,5));
        content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        try {
            int reservations = service.getReservationCount();
            int cancels = service.getCancellationCount();
            int noShows = service.getNoShowCount();
            int attended = service.getAttendedCount();
            double cancelRate = reservations + cancels == 0 ? 0 : (double)cancels/(reservations+cancels)*100.0;
            double noShowRate = attended + noShows == 0 ? 0 : (double)noShows/(attended+noShows)*100.0;
            content.add(new JLabel("Aktuelle Reservierungen: " + reservations));
            content.add(new JLabel("Stornierungen gesamt: " + cancels));
            content.add(new JLabel("Stornierungsrate: " + String.format("%.1f%%", cancelRate)));
            content.add(new JLabel("No-Shows gesamt: " + noShows));
            content.add(new JLabel("No-Show-Rate: " + String.format("%.1f%%", noShowRate)));
            List<String> popular = service.getPopularTimes(3);
            content.add(new JLabel("Beliebteste Zeiten: " + String.join(", ", popular)));
        } catch (Exception e) {
            content.add(new JLabel("Fehler beim Laden der Statistiken."));
        }
        add(content);
    }
}
