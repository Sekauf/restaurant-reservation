package com.restaurant.reservation.dao;

import com.restaurant.reservation.model.Reservation;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;

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
        String reservationsSql = "CREATE TABLE IF NOT EXISTS reservations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "date TEXT NOT NULL," +
                "time TEXT NOT NULL," +
                "persons INTEGER NOT NULL," +
                "table_number INTEGER NOT NULL," +
                "status TEXT NOT NULL DEFAULT 'PENDING'," +
                "created_at TEXT NOT NULL DEFAULT (datetime('now'))," +
                "confirmed_at TEXT)";

        String cancelsSql = "CREATE TABLE IF NOT EXISTS cancellations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "reservation_id INTEGER NOT NULL," +
                "cancelled_at TEXT NOT NULL)";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(reservationsSql);
            stmt.executeUpdate(cancelsSql);

            // Zusätzliche Spalten ergänzen (Kompatibilität mit älteren DB-Versionen)
            boolean hasStatus = false;
            boolean hasCreated = false;
            boolean hasConfirmed = false;
            try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(reservations)")) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    if ("status".equalsIgnoreCase(name)) {
                        hasStatus = true;
                    }
                    if ("created_at".equalsIgnoreCase(name)) {
                        hasCreated = true;
                    }
                    if ("confirmed_at".equalsIgnoreCase(name)) {
                        hasConfirmed = true;
                    }
                }
            }
            if (!hasStatus) {
                stmt.executeUpdate("ALTER TABLE reservations ADD COLUMN status TEXT NOT NULL DEFAULT 'PENDING'");
            }
            if (!hasCreated) {
                stmt.executeUpdate("ALTER TABLE reservations ADD COLUMN created_at TEXT");
                stmt.executeUpdate("UPDATE reservations SET created_at = datetime('now') WHERE created_at IS NULL");
            }
            if (!hasConfirmed) {
                stmt.executeUpdate("ALTER TABLE reservations ADD COLUMN confirmed_at TEXT");
            }
        } catch (SQLException e) {
            System.err.println("Fehler beim Erstellen der Datenbanktabelle: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Importiert Beispielreservierungen, falls die Tabelle leer ist.
     */
    public static void importSampleDataIfEmpty() {
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM reservations")) {
            if (rs.next() && rs.getInt(1) == 0) {
                try (java.io.InputStream in = ReservationDAO.class.getResourceAsStream("/sql/sample_reservations.sql")) {
                    if (in != null) {
                        String sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                        stmt.executeUpdate(sql);
                    } else {
                        System.err.println("Beispieldatei sample_reservations.sql nicht gefunden");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Fehler beim Import der Beispielreservierungen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Importiert die Beispieldaten aus einer Datei und löscht diese anschließend.
     * Damit kann die Datei "sql/sample_reservations.sql" in die bestehende
     * Datenbank gemerged werden.
     * @param path Pfad zur SQL-Datei mit den Beispielreservierungen
     */
    public static void mergeSampleDataFromFile(String path) {
        java.nio.file.Path p = java.nio.file.Paths.get(path);
        if (!java.nio.file.Files.exists(p)) {
            return;
        }
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = java.nio.file.Files.readString(p, java.nio.charset.StandardCharsets.UTF_8);
            stmt.executeUpdate(sql);
            java.nio.file.Files.delete(p); // Datei nach Import entfernen
        } catch (Exception e) {
            System.err.println("Fehler beim Mergen der Beispieldaten: " + e.getMessage());
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
        String sql = "SELECT id, name, date, time, persons, table_number, status FROM reservations ORDER BY date, time";
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
                        rs.getInt("table_number"),
                        rs.getString("status")
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
        String sql = "INSERT INTO reservations(name, date, time, persons, table_number, status, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, datetime('now'))";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reservation.getName());
            ps.setString(2, reservation.getDate());
            ps.setString(3, reservation.getTime());
            ps.setInt(4, reservation.getPersons());
            ps.setInt(5, reservation.getTableNumber());
            ps.setString(6, reservation.getStatus());
            ps.executeUpdate();
        }
    }

    /**
     * Löscht die Reservierung mit der gegebenen ID aus der Datenbank.
     * @param id die ID der zu löschenden Reservierung
     * @throws SQLException falls ein Fehler beim Löschen auftritt
     */
    public void deleteReservation(int id) throws SQLException {
        String insertCancel = "INSERT INTO cancellations(reservation_id, cancelled_at) VALUES (?, datetime('now'))";
        String deleteRes = "DELETE FROM reservations WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ins = conn.prepareStatement(insertCancel);
             PreparedStatement del = conn.prepareStatement(deleteRes)) {
            ins.setInt(1, id);
            ins.executeUpdate();

            del.setInt(1, id);
            del.executeUpdate();
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
                return rs.next();
            }
        }
    }

    /** Zählt alle aktuellen Reservierungen. */
    public int countReservations() throws SQLException {
        String sql = "SELECT COUNT(*) FROM reservations";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /** Zählt alle stornierten Reservierungen. */
    public int countCancellations() throws SQLException {
        String sql = "SELECT COUNT(*) FROM cancellations";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Ermittelt die beliebtesten Uhrzeiten anhand der Anzahl an Reservierungen.
     * @param limit maximale Anzahl von Ergebnissen
     */
    public List<String> findPopularTimes(int limit) throws SQLException {
        List<String> result = new ArrayList<>();
        String sql = "SELECT time, COUNT(*) as cnt FROM reservations GROUP BY time ORDER BY cnt DESC LIMIT ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String time = rs.getString("time");
                    result.add(time);
                }
            }
        }
        return result;
    }

    /**
     * Liefert alle Reservierungen für einen bestimmten Tisch.
     *
     * @param tableNumber die Tisch-Nummer
     * @return Liste der Reservierungen für diesen Tisch
     * @throws SQLException falls ein Datenbankfehler auftritt
     */
    public List<Reservation> getReservationsForTable(int tableNumber) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT id, name, date, time, persons, table_number, status FROM reservations " +
                     "WHERE table_number = ? ORDER BY date, time";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tableNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reservation res = new Reservation(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("date"),
                            rs.getString("time"),
                            rs.getInt("persons"),
                            rs.getInt("table_number"),
                            rs.getString("status")
                    );
                    list.add(res);
                }
            }
        }
        return list;
    }

    /** Aktualisiert den Status einer Reservierung. */
    public void updateStatus(int id, String status) throws SQLException {
        String sql = "UPDATE reservations SET status = ?, " +
                     "confirmed_at = CASE WHEN confirmed_at IS NULL AND ? <> 'PENDING' THEN datetime('now') ELSE confirmed_at END " +
                     "WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, status);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    /** Zählt Reservierungen mit Status 'NOSHOW'. */
    public int countNoShows() throws SQLException {
        String sql = "SELECT COUNT(*) FROM reservations WHERE status = 'NOSHOW'";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /** Zählt Reservierungen mit Status 'ATTENDED'. */
    public int countAttended() throws SQLException {
        String sql = "SELECT COUNT(*) FROM reservations WHERE status = 'ATTENDED'";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /** Durchschnittliche Reservierungen pro Tag. */
    public double averageReservationsPerDay() throws SQLException {
        String sql = "SELECT AVG(cnt) FROM (SELECT date, COUNT(*) AS cnt FROM reservations GROUP BY date)";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }

    /** Durchschnittliche Auslastung aller Zeitslots in Prozent. */
    public double averageOccupancy() throws SQLException {
        int tables;
        String tableSql = "SELECT COUNT(*) FROM tables";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(tableSql)) {
            tables = rs.next() ? rs.getInt(1) : 0;
        }
        if (tables == 0) return 0.0;
        String occSql = "SELECT AVG(cnt) FROM (SELECT date, time, COUNT(*) AS cnt FROM reservations GROUP BY date, time)";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(occSql)) {
            double avg = rs.next() ? rs.getDouble(1) : 0.0;
            return avg / tables * 100.0;
        }
    }

    /** Durchschnittliche Zeit in Stunden zwischen Buchung und Termin. */
    public double averageLeadTimeHours() throws SQLException {
        String sql = "SELECT AVG(strftime('%s', date || ' ' || time) - strftime('%s', created_at)) / 3600.0 FROM reservations";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }

    /** Durchschnittliche Zeit in Stunden zwischen Buchung und Bestätigung. */
    public double averageProcessingTimeHours() throws SQLException {
        String sql = "SELECT AVG(strftime('%s', confirmed_at) - strftime('%s', created_at)) / 3600.0 FROM reservations WHERE confirmed_at IS NOT NULL";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }
}
