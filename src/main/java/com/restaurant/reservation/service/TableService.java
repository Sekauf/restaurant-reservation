package com.restaurant.reservation.service;

import com.restaurant.reservation.dao.TableDAO;
import com.restaurant.reservation.model.Table;
import java.sql.SQLException;
import java.util.List;

/**
 * Service-Schicht für die Verwaltung von Tisch-Daten.
 * Kapselt die Datenzugriffsschicht (DAO) und ergänzt Geschäftslogik, z.B. Prüfungen vor Löschaktionen.
 */
public class TableService {

    private TableDAO tableDAO;

    public TableService() {
        this.tableDAO = new TableDAO();
    }

    /**
     * Gibt alle Tische zurück.
     * @return Liste der Tisch-Objekte
     * @throws SQLException falls beim Datenbankzugriff ein Fehler auftritt
     */
    public List<Table> getAllTables() throws SQLException {
        return tableDAO.findAllTables();
    }

    /**
     * Speichert einen neuen Tisch.
     * @param table der neue Tisch (ohne ID)
     * @return der gespeicherte Tisch mit gesetzter ID
     * @throws SQLException falls beim Einfügen ein Fehler auftritt
     */
    public Table addTable(Table table) throws SQLException {
        // Hier könnte man z.B. Validierungen durchführen (nicht implementiert)
        return tableDAO.insert(table);
    }

    /**
     * Aktualisiert einen vorhandenen Tisch.
     * @param table der zu aktualisierende Tisch (mit gültiger ID)
     * @return true, wenn erfolgreich aktualisiert, sonst false
     * @throws SQLException falls beim Aktualisieren ein Fehler auftritt
     */
    public boolean updateTable(Table table) throws SQLException {
        // Eventuelle Geschäftslogik (z.B. Validierung) könnte hier erfolgen
        return tableDAO.update(table);
    }

    /**
     * Löscht einen Tisch, falls er nicht in Reservierungen verwendet wird.
     * @param id die ID des zu löschenden Tischs
     * @throws IllegalStateException falls der Tisch noch in Reservierungen genutzt wird
     * @throws SQLException falls ein Datenbankfehler auftritt
     */
    public void deleteTable(int id) throws SQLException {
        // Prüfen, ob der Tisch in einer Reservierung vorkommt
        if (tableDAO.isTableUsed(id)) {
            throw new IllegalStateException("Der Tisch mit ID " + id + " kann nicht gelöscht werden, da er in Reservierungen verwendet wird.");
        }
        // Wenn nicht verwendet, löschen
        tableDAO.delete(id);
    }

    /**
     * Liefert einen Tisch anhand seiner ID.
     *
     * @param id die Tisch-ID
     * @return das Tisch-Objekt oder {@code null}, wenn kein Tisch existiert
     * @throws SQLException falls beim Datenbankzugriff ein Fehler auftritt
     */
    public Table getTableById(int id) throws SQLException {
        return tableDAO.findTableById(id);
    }
}
