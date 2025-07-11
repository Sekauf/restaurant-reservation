package com.restaurant.reservation.model;

/**
 * Model-Klasse für eine Tischreservierung im Restaurant.
 * Enthält die Reservierungsdaten als Eigenschaften.
 */
public class Reservation {
    private Integer id;
    private String name;
    /** Reservierungsdatum (Format YYYY-MM-DD) */
    private String date;
    /** Reservierungsuhrzeit (Format HH:MM) */
    private String time;
    private int persons;
    private int tableNumber;
    private String status;


    public Reservation(String name, String date, String time, int persons, int tableNumber) {
        this(null, name, date, time, persons, tableNumber, "PENDING");
    }

    public Reservation(Integer id, String name, String date, String time, int persons, int tableNumber, String status) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.persons = persons;
        this.tableNumber = tableNumber;
        this.status = status;
    }

    /**
     * Kompatibilitätskonstruktor ohne Status.
     */
    public Reservation(Integer id, String name, String date, String time, int persons, int tableNumber) {
        this(id, name, date, time, persons, tableNumber, "PENDING");
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
