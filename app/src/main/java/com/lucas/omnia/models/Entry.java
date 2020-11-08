package com.lucas.omnia.models;

public class Entry {
    public final String type;
    public final String date;
    public final String urn;
    public final String locality;
    public final String authority;
    public final String title;
    public final String subject;
    public final String description;

    public Entry(String type, String date, String urn, String locality, String authority, String title,
                 String subject,
                 String description) {
        this.type = type;
        this.date = date;
        this.urn = urn;
        this.locality = locality;
        this.authority = authority;
        this.title = title;
        this.subject = subject;
        this.description = description;
    }
}
