package com.restaurant.reservation.dao;

import com.restaurant.reservation.model.Reservation;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO-Klasse für den Zugriff auf Reservierungsdaten (SQLite-Datenbank).
 * Enthält CRUD-Methoden für die Tabelle "reservations".
 */
public class ReservationDAO {

    /**
     * Legt die Tabelle "reservations" in der Datenbank an, falls sie noch nicht existiert.
     * Wird typischerweise beim Programmstart einmalig aufgerufen.
     */
    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS reservations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "date TEXT NOT NULL," +
                "time TEXT NOT NULL," +
                "persons INTEGER NOT NULL," +
                "table_number INTEGER NOT NULL)";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Fehler beim Erstellen der Datenbanktabelle: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Liest alle Reservierungen aus der Datenbank.
     * @return Liste aller Reservierungen (als Reservation-Objekte)
     * @throws SQLException falls ein Datenbankfehler auftritt
     */
    public List<Reservation> getAllReservations() throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT id, name, date, time, persons, table_number FROM reservations ORDER BY date, time";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Reservation res = new Reservation(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getInt("persons"),
                        rs.getInt("table_number")
                );
                list.add(res);
            }
        }
        return list;
    }

    /**
     * Fügt eine neue Reservierung in die Datenbank ein.
     * @param reservation das Reservation-Objekt mit den zu speichernden Daten (ohne ID)
     * @throws SQLException falls ein Fehler beim Einfügen auftritt
     */
    public void addReservation(Reservation reservation) throws SQLException {
        String sql = "INSERT INTO reservations(name, date, time, persons, table_number) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reservation.getName());
            ps.setString(2, reservation.getDate());
            ps.setString(3, reservation.getTime());
            ps.setInt(4, reservation.getPersons());
            ps.setInt(5, reservation.getTableNumber());
            ps.executeUpdate();
        }
    }

    /**
     * Löscht die Reservierung mit der gegebenen ID aus der Datenbank.
     * @param id die ID der zu löschenden Reservierung
     * @throws SQLException falls ein Fehler beim Löschen auftritt
     */
    public void deleteReservation(int id) throws SQLException {
        String sql = "DELETE FROM reservations WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Prüft, ob für eine gegebene Kombination aus Datum, Uhrzeit und Tisch-Nr bereits eine Reservierung existiert.
     * @param date Datum der Reservierung (Format YYYY-MM-DD)
     * @param time Uhrzeit der Reservierung (Format HH:MM)
     * @param tableNumber Tisch-Nummer
     * @return true, falls bereits eine Reservierung für diesen Tisch zu der Zeit existiert, sonst false
     * @throws SQLException bei Datenbankfehlern
     */
    public boolean existsReservation(String date, String time, int tableNumber) throws SQLException {
        String sql = "SELECT id FROM reservations WHERE date = ? AND time = ? AND table_number = ? LIMIT 1";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, date);
            ps.setString(2, time);
            ps.setInt(3, tableNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();  // true, wenn mindestens ein Treffer existiert
            }
        }
    }
}
