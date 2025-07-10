package com.restaurant.reservation.ui;

import com.restaurant.reservation.service.ReservationService;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Einfaches Fenster zur Anzeige einiger KPIs und Statistiken.
 */
public class StatisticsFrame extends JFrame {
    private final ReservationService service;

    // Felder zum Speichern der berechneten Statistiken
    private int reservations;
    private int cancels;
    private int noShows;
    private int attended;
    private double cancelRate;
    private double noShowRate;
    private List<String> popular;

    public StatisticsFrame(ReservationService service) {
        this.service = service;
        setTitle("Statistiken");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buildUi();
        setSize(400,300);
        setLocationRelativeTo(null);
    }

    private void buildUi() {
        JPanel wrapper = new JPanel(new BorderLayout());
        // Panel mit etwas Rand, damit die Labels nicht direkt am Rand kleben
        JPanel content = new JPanel(new GridLayout(0,1,5,5));
        content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        try {
            reservations = service.getReservationCount();
            cancels = service.getCancellationCount();
            noShows = service.getNoShowCount();
            attended = service.getAttendedCount();
            cancelRate = reservations + cancels == 0 ? 0 : (double)cancels/(reservations+cancels)*100.0;
            noShowRate = attended + noShows == 0 ? 0 : (double)noShows/(attended+noShows)*100.0;
            content.add(new JLabel("Aktuelle Reservierungen: " + reservations));
            content.add(new JLabel("Stornierungen gesamt: " + cancels));
            content.add(new JLabel("Stornierungsrate: " + String.format("%.1f%%", cancelRate)));
            content.add(new JLabel("No-Shows gesamt: " + noShows));
            content.add(new JLabel("No-Show-Rate: " + String.format("%.1f%%", noShowRate)));
            popular = service.getPopularTimes(3);
            content.add(new JLabel("Beliebteste Zeiten: " + String.join(", ", popular)));
        } catch (Exception e) {
            content.add(new JLabel("Fehler beim Laden der Statistiken."));
        }

        JButton exportButton = new JButton("Als CSV exportieren");
        exportButton.addActionListener(e -> exportCsv());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(exportButton);

        wrapper.add(content, BorderLayout.CENTER);
        wrapper.add(buttonPanel, BorderLayout.SOUTH);
        add(wrapper);
    }

    /** Exportiert die angezeigten Statistiken in eine CSV-Datei. */
    private void exportCsv() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8)) {
                pw.println("Kennzahl,Wert");
                pw.println("Aktuelle Reservierungen," + reservations);
                pw.println("Stornierungen gesamt," + cancels);
                pw.println("Stornierungsrate," + String.format("%.1f%%", cancelRate));
                pw.println("No-Shows gesamt," + noShows);
                pw.println("No-Show-Rate," + String.format("%.1f%%", noShowRate));
                if (popular != null) {
                    pw.println("Beliebteste Zeiten,\"" + String.join(" | ", popular) + "\"");
                }
                JOptionPane.showMessageDialog(this, "Export erfolgreich.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Fehler beim Export.", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
