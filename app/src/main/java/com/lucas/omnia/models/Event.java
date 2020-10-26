package com.lucas.omnia.models;

public class Event {

    private final String date;
    private final String status;
    private final String type;
    private final String description;
    private final String name;
    private final String building;
    private final String room;
    private final String floor;

    public Event(String date, String status, String type, String description, String name,
                 String building, String room, String floor) {
        this.date = date;
        this.status = status;
        this.type = type;
        this.description = description;
        this.name = name;
        this.building = building;
        this.room = room;
        this.floor = floor;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getBuilding() {
        return building;
    }

    public String getRoom() {
        return room;
    }

    public String getFloor() {
        return floor;
    }
}
