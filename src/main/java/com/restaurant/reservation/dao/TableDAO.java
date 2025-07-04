package com.restaurant.reservation.dao;

import com.restaurant.reservation.model.Table;
import com.restaurant.reservation.dao.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO-Klasse für den Datenzugriff auf die Tisch-Tabelle in der SQLite-Datenbank.
 * Stellt Methoden zum Finden, Einfügen, Aktualisieren und Löschen von Tischen bereit.
 */
public class TableDAO {

    /** Stellt eine Verbindung zur SQLite-Datenbank her. */
    private Connection connect() throws SQLException {
        return Database.getConnection();
    }

    /**
     * Legt die Tabelle "tables" an, falls sie noch nicht existiert.
     */
    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS tables (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "seats INTEGER NOT NULL," +
                "hasProjector INTEGER NOT NULL)";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Fehler beim Erstellen der Tisch-Tabelle: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Liest alle Tische aus der Datenbank.
     * @return Liste aller Tische
     * @throws SQLException falls ein DB-Zugriffsfehler auftritt
     */
    public List<Table> findAllTables() throws SQLException {
        List<Table> tables = new ArrayList<>();
        String sql = "SELECT id, name, seats, hasProjector FROM tables";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                // Ergebnissatz auslesen und Table-Objekt erstellen
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int seats = rs.getInt("seats");
                // SQLite speichert boolesche Werte typischerweise als 0/1
                boolean hasProjector = rs.getInt("hasProjector") == 1;
                Table table = new Table(id, name, seats, hasProjector);
                tables.add(table);
            }
        }
        return tables;
    }

    /**
     * Fügt einen neuen Tisch in die Datenbank ein.
     * @param table das Tisch-Objekt ohne ID (ID wird von der DB vergeben)
     * @return das gespeicherte Tisch-Objekt mit gesetzter ID
     * @throws SQLException falls ein DB-Zugriffsfehler auftritt
     */
    public Table insert(Table table) throws SQLException {
        String sql = "INSERT INTO tables (name, seats, hasProjector) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, table.getName());
            stmt.setInt(2, table.getSeats());
            // Wandelt boolean zu 0/1 für SQLite um
            stmt.setInt(3, table.isHasProjector() ? 1 : 0);
            stmt.executeUpdate();
            // Generierte ID abrufen
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int newId = keys.getInt(1);
                    table.setId(newId);
                }
            }
        }
        return table;
    }

    /**
     * Aktualisiert einen bestehenden Tisch in der Datenbank.
     * @param table das Tisch-Objekt mit aktualisierten Werten (die ID gibt den zu ändernden Datensatz an)
     * @return true, wenn ein Datensatz aktualisiert wurde, sonst false
     * @throws SQLException falls ein DB-Zugriffsfehler auftritt
     */
    public boolean update(Table table) throws SQLException {
        String sql = "UPDATE tables SET name = ?, seats = ?, hasProjector = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, table.getName());
            stmt.setInt(2, table.getSeats());
            stmt.setInt(3, table.isHasProjector() ? 1 : 0);
            stmt.setInt(4, table.getId());
            int affected = stmt.executeUpdate();
            return affected > 0;
        }
    }

    /**
     * Löscht einen Tisch anhand seiner ID.
     * @param id die ID des zu löschenden Tischs
     * @return true, wenn der Tisch gelöscht wurde (bzw. existierte), sonst false
     * @throws SQLException falls ein DB-Zugriffsfehler auftritt
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM tables WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int affected = stmt.executeUpdate();
            return affected > 0;
        }
    }

    /**
     * Prüft, ob ein gegebener Tisch (ID) in einer Reservierung verwendet wird.
     * @param id die Tisch-ID
     * @return true, falls der Tisch in mindestens einer Reservierung vorkommt, sonst false
     * @throws SQLException falls ein DB-Zugriffsfehler auftritt
     */
    public boolean isTableUsed(int id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reservations WHERE table_number = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }
}
