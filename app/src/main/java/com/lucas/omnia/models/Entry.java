package com.lucas.omnia.models;

public class Entry {
    public final String type;
    public final String date;
    public final String title;
    public final String subject;
    public final String description;

    public Entry(String type, String date, String title, String subject, String description) {
        this.type = type;
        this.date = date;
        this.title = title;
        this.subject = subject;
        this.description = description;
    }
}
