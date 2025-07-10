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
