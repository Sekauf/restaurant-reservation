package com.restaurant.reservation.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;

/**
 * Hilfsklasse für die Verwaltung der Datenbankverbindung zur SQLite-DB.
 */
public class Database {
    private static final String DB_URL = "jdbc:sqlite:db/restaurant.db";

    static {
        try {
            // SQLite JDBC-Treiber laden (nicht zwingend erforderlich ab JDBC 4, aber zur Sicherheit)
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stellt eine Verbindung zur SQLite-Datenbank her.
     * @return offene JDBC Connection
     * @throws SQLException bei Verbindungsfehlern
     */
    public static Connection getConnection() throws SQLException {
        // Ensure the directory for the database file exists
        File dbDir = new File("db");
        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }
        return DriverManager.getConnection(DB_URL);
    }
}
