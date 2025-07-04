package com.restaurant.reservation.model;

/**
 * Model-Klasse für eine Tischreservierung im Restaurant.
 * Enthält die Reservierungsdaten als Eigenschaften.
 */
public class Reservation {
    /** Primärschlüssel der Reservierung (wird von der Datenbank vergeben) */
    private Integer id;
    /** Name des Kunden/Gasts */
    private String name;
    /** Reservierungsdatum (Format YYYY-MM-DD) */
    private String date;
    /** Reservierungsuhrzeit (Format HH:MM) */
    private String time;
    /** Anzahl der Personen für die Reservierung */
    private int persons;
    /** Tisch-Nummer im Restaurant */
    private int tableNumber;

    /**
     * Konstruktor für eine neue Reservierung (ohne ID, z.B. vor Datenbank-Speicherung).
     */
    public Reservation(String name, String date, String time, int persons, int tableNumber) {
        this(null, name, date, time, persons, tableNumber);
    }

    /**
     * Konstruktor mit allen Attributen.
     * @param id die Reservierungs-ID (Primary Key)
     * @param name Name des Kunden
     * @param date Datum der Reservierung
     * @param time Uhrzeit der Reservierung
     * @param persons Anzahl der Personen
     * @param tableNumber Tisch-Nummer
     */
    public Reservation(Integer id, String name, String date, String time, int persons, int tableNumber) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.persons = persons;
        this.tableNumber = tableNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getPersons() {
        return persons;
    }

    public void setPersons(int persons) {
        this.persons = persons;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }
}
