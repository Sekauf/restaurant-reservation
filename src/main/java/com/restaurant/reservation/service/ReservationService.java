package com.restaurant.reservation.service;

import com.restaurant.reservation.dao.ReservationDAO;
import com.restaurant.reservation.model.Reservation;
import java.sql.SQLException;

/**
 * Service-Schicht für Geschäftslogik rund um Reservierungen.
 * Kapselt die Datenbankzugriffe und führt Plausibilitätsprüfungen durch.
 */
public class ReservationService {
    private ReservationDAO dao;

    public ReservationService() {
        this.dao = new ReservationDAO();
    }

    /**
     * Holt alle bestehenden Reservierungen.
     * @return Liste der Reservierungen
     * @throws Exception falls die Daten nicht geladen werden konnten
     */
    public java.util.List<Reservation> getAllReservations() throws Exception {
        try {
            return dao.getAllReservations();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Datenbankfehler beim Laden der Reservierungen.", e);
        }
    }

    /**
     * Legt eine neue Reservierung mit den angegebenen Daten an.
     * Prüft auf Doppelbuchungen und speichert dann über den DAO.
     * @param name Name des Kunden
     * @param date Datum (YYYY-MM-DD)
     * @param time Uhrzeit (HH:MM)
     * @param persons Anzahl der Personen
     * @param tableNumber Tisch-Nummer
     * @throws Exception bei fachlichen Fehlern (z.B. Doppelbuchung) oder Datenbankfehlern
     */
    public void addReservation(String name, String date, String time, int persons, int tableNumber) throws Exception {
        try {
            if (dao.existsReservation(date, time, tableNumber)) {
                throw new Exception("Dieser Tisch ist zu diesem Zeitpunkt bereits reserviert.");
            }
            Reservation res = new Reservation(name, date, time, persons, tableNumber);
            dao.addReservation(res);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Datenbankfehler beim Speichern der Reservierung.", e);
        }
    }

    /**
     * Löscht die Reservierung mit der angegebenen ID.
     * @param reservationId die ID der zu löschenden Reservierung
     * @throws Exception falls beim Löschen ein Fehler auftritt
     */
    public void deleteReservation(int reservationId) throws Exception {
        try {
            dao.deleteReservation(reservationId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Datenbankfehler beim Löschen der Reservierung.", e);
        }
    }

    /**
     * Prüft, ob ein Tisch für den angegebenen Zeitpunkt bereits reserviert ist.
     * @param date Datum (YYYY-MM-DD)
     * @param time Uhrzeit (HH:MM)
     * @param tableNumber Tisch-Nr
     * @return true, wenn bereits eine Reservierung existiert
     * @throws Exception bei Datenbankfehlern
     */
    public boolean isTableReserved(String date, String time, int tableNumber) throws Exception {
        try {
            return dao.existsReservation(date, time, tableNumber);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Datenbankfehler beim Prüfen der Reservierung.", e);
        }
    }

    /** Liefert die Anzahl aktueller Reservierungen. */
    public int getReservationCount() throws Exception {
        try {
            return dao.countReservations();
        } catch (SQLException e) {
            throw new Exception("Datenbankfehler beim Zählen der Reservierungen.", e);
        }
    }

    /** Liefert die Anzahl stornierter Reservierungen. */
    public int getCancellationCount() throws Exception {
        try {
            return dao.countCancellations();
        } catch (SQLException e) {
            throw new Exception("Datenbankfehler beim Zählen der Stornierungen.", e);
        }
    }

    /** Beliebteste Uhrzeiten (nach Anzahl Reservierungen). */
    public java.util.List<String> getPopularTimes(int limit) throws Exception {
        try {
            return dao.findPopularTimes(limit);
        } catch (SQLException e) {
            throw new Exception("Datenbankfehler beim Ermitteln der Uhrzeiten.", e);
        }
    }

    /**
     * Gibt alle Reservierungen für einen bestimmten Tisch zurück.
     *
     * @param tableNumber die Tisch-Nummer
     * @return Liste der Reservierungen
     * @throws Exception falls ein Datenbankfehler auftritt
     */
    public java.util.List<Reservation> getReservationsForTable(int tableNumber) throws Exception {
        try {
            return dao.getReservationsForTable(tableNumber);
        } catch (SQLException e) {
            throw new Exception("Datenbankfehler beim Laden der Reservierungen.", e);
        }
    }

    /** Setzt den Status einer Reservierung. */
    public void setStatus(int reservationId, String status) throws Exception {
        try {
            dao.updateStatus(reservationId, status);
        } catch (SQLException e) {
            throw new Exception("Datenbankfehler beim Aktualisieren des Status.", e);
        }
    }

    /** Anzahl der No-Shows. */
    public int getNoShowCount() throws Exception {
        try {
            return dao.countNoShows();
        } catch (SQLException e) {
            throw new Exception("Datenbankfehler beim Zählen der No-Shows.", e);
        }
    }

    /** Anzahl der als erschienen markierten Reservierungen. */
    public int getAttendedCount() throws Exception {
        try {
            return dao.countAttended();
        } catch (SQLException e) {
            throw new Exception("Datenbankfehler beim Zählen der Besuche.", e);
        }
    }
}
