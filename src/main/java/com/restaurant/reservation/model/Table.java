package com.restaurant.reservation.model;

/**
 * Datenklasse für einen Tisch mit ID, Name, Sitzplatzanzahl und Projektor-Ausstattung.
 */
public class Table {
    private int id;
    private String name;
    private int seats;
    private boolean hasProjector;

    /**
     * Standard-Konstruktor (erzeugt einen leeren Tisch).
     */
    public Table() {
    }

    /**
     * Konstruktor zur Initialisierung aller Attribute eines Tischs.
     * @param id            die eindeutige ID des Tischs (Primärschlüssel)
     * @param name          die Bezeichnung des Tischs
     * @param seats         Anzahl der Sitzplätze am Tisch
     * @param hasProjector  gibt an, ob ein fester Projektor am Tisch vorhanden ist
     */
    public Table(int id, String name, int seats, boolean hasProjector) {
        this.id = id;
        this.name = name;
        this.seats = seats;
        this.hasProjector = hasProjector;
    }

    /**
     * Konstruktor ohne ID (für neue Tische, ID wird von der DB vergeben).
     * @param name          die Bezeichnung des Tischs
     * @param seats         Anzahl der Sitzplätze am Tisch
     * @param hasProjector  gibt an, ob ein fester Projektor am Tisch vorhanden ist
     */
    public Table(String name, int seats, boolean hasProjector) {
        this(0, name, seats, hasProjector);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public boolean isHasProjector() {
        return hasProjector;
    }

    public void setHasProjector(boolean hasProjector) {
        this.hasProjector = hasProjector;
    }

    @Override
    public String toString() {
        return "Table{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", seats=" + seats +
                ", hasProjector=" + hasProjector +
                '}';
    }
}
