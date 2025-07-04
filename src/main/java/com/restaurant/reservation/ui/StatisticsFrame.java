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
        setLayout(new GridLayout(0,1,5,5));
        try {
            int reservations = service.getReservationCount();
            int cancels = service.getCancellationCount();
            double cancelRate = reservations + cancels == 0 ? 0 : (double)cancels/(reservations+cancels)*100.0;
            add(new JLabel("Aktuelle Reservierungen: " + reservations));
            add(new JLabel("Stornierungen gesamt: " + cancels));
            add(new JLabel("Stornierungsrate: " + String.format("%.1f%%", cancelRate)));
            List<String> popular = service.getPopularTimes(3);
            add(new JLabel("Beliebteste Zeiten: " + String.join(", ", popular)));
        } catch (Exception e) {
            add(new JLabel("Fehler beim Laden der Statistiken."));
        }
    }
}
