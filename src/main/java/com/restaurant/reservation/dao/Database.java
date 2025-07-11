package com.restaurant.reservation.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;

/**
 * Hilfsklasse für die Verwaltung der Datenbankverbindung zur SQLite-DB.
 */
public class Database {
    /** Pfad zur SQLite-Datenbank. */
    private static final String DB_URL = "jdbc:sqlite:db/restaurant.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        File dir = new File("db");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return DriverManager.getConnection(DB_URL);
    }
}
