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
        // Prüfen, ob für Datum/Zeit/Tisch bereits eine Reservierung existiert
        try {
            if (dao.existsReservation(date, time, tableNumber)) {
                throw new Exception("Dieser Tisch ist zu diesem Zeitpunkt bereits reserviert.");
            }
            // Falls frei, Reservierung in DB speichern
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
}
