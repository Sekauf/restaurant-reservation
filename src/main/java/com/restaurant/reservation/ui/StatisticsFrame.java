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

    private int reservations;
    private int noShows;
    private int attended;
    private double noShowRate;
    private double avgPerDay;
    private double avgOccupancy;
    private double avgLeadTime;
    private double avgProcessing;
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
        JPanel content = new JPanel(new GridLayout(0,1,5,5));
        content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        try {
            reservations = service.getReservationCount();
            noShows = service.getNoShowCount();
            attended = service.getAttendedCount();
            avgPerDay = service.getAverageReservationsPerDay();
            avgOccupancy = service.getAverageOccupancy();
            avgLeadTime = service.getAverageLeadTimeHours();
            avgProcessing = service.getAverageProcessingTimeHours();
            noShowRate = attended + noShows == 0 ? 0 : (double)noShows/(attended+noShows)*100.0;
            content.add(new JLabel("Aktuelle Reservierungen: " + reservations));
            content.add(new JLabel("No-Shows gesamt: " + noShows));
            content.add(new JLabel("No-Show-Rate: " + String.format("%.1f%%", noShowRate)));
            content.add(new JLabel(String.format("Ø Reservierungen/Tag: %.2f", avgPerDay)));
            content.add(new JLabel(String.format("Ø Auslastung: %.1f%%", avgOccupancy)));
            content.add(new JLabel(String.format("Ø Buchungsvorlauf: %.1f Std", avgLeadTime)));
            content.add(new JLabel(String.format("Ø Bearbeitungszeit: %.1f Std", avgProcessing)));
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
                pw.println("No-Shows gesamt," + noShows);
                pw.println("No-Show-Rate," + String.format("%.1f%%", noShowRate));
                pw.println(String.format("Ø Reservierungen/Tag,%.2f", avgPerDay));
                pw.println(String.format("Ø Auslastung,%.1f%%", avgOccupancy));
                pw.println(String.format("Ø Buchungsvorlauf,%.1f Std", avgLeadTime));
                pw.println(String.format("Ø Bearbeitungszeit,%.1f Std", avgProcessing));
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
