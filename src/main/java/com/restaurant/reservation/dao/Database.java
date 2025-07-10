package com.restaurant.reservation.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;

/**
 * Hilfsklasse für die Verwaltung der Datenbankverbindung zur SQLite-DB.
 */
public class Database {
    private static final String DB_URL;

    static {
        String dbPath;
        try {
            // Ablageverzeichnis anhand des Speicherorts der Klassen oder des
            // ausführbaren JAR bestimmen
            java.net.URI uri = Database.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI();
            File baseDir = new File(uri);
            // Bei Ausführung aus einem JAR liegt hier die JAR-Datei, deshalb
            // ggf. auf das enthaltende Verzeichnis wechseln
            if (baseDir.isFile()) {
                baseDir = baseDir.getParentFile();
            }
            // Die Datenbank soll direkt unterhalb dieses Verzeichnisses in
            // einem Unterordner "db" liegen
            File dbDir = new File(baseDir, "db");
            if (!dbDir.exists()) {
                dbDir.mkdirs();
            }
            dbPath = new File(dbDir, "restaurant.db").getAbsolutePath();
        } catch (Exception e) {
            // Fallback: aktuelles Arbeitsverzeichnis verwenden
            File dbDir = new File("db");
            if (!dbDir.exists()) {
                dbDir.mkdirs();
            }
            dbPath = new File(dbDir, "restaurant.db").getAbsolutePath();
        }
        DB_URL = "jdbc:sqlite:" + dbPath;

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
        return DriverManager.getConnection(DB_URL);
    }
}
