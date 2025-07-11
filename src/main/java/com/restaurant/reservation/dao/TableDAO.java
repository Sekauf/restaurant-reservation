package com.restaurant.reservation.dao;

import com.restaurant.reservation.model.Table;
import com.restaurant.reservation.dao.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class TableDAO {

    /** Stellt eine Verbindung zur SQLite-Datenbank her. */
    private Connection connect() throws SQLException {
        return Database.getConnection();
    }

    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS tables (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "seats INTEGER NOT NULL," +
                "hasProjector INTEGER NOT NULL)";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);

            // Beispieldaten einfügen, wenn noch keine Tische existieren
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM tables")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String insert = "INSERT INTO tables (name, seats, hasProjector) VALUES (?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(insert)) {
                        for (int i = 1; i <= 15; i++) {
                            ps.setString(1, "Tisch " + i);
                            ps.setInt(2, 2 + (i % 7)); // Sitzplätze 2-8
                            ps.setInt(3, 0); // kein Projektor
                            ps.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Fehler beim Erstellen der Tisch-Tabelle: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public List<Table> findAllTables() throws SQLException {
        List<Table> tables = new ArrayList<>();
        String sql = "SELECT id, name, seats, hasProjector FROM tables";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
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



    public Table findTableById(int id) throws SQLException {
        String sql = "SELECT id, name, seats, hasProjector FROM tables WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    int seats = rs.getInt("seats");
                    boolean hasProjector = rs.getInt("hasProjector") == 1;
                    return new Table(id, name, seats, hasProjector);
                }
            }
        }
        return null;
    }
}
