package com.lucas.omnia.models;

public class Law {
    public final String urn;
    public final String locale;
    public final String authority;
    public final String title;
    public final String description;

    public Law(String urn, String locale, String authority, String title, String description) {
        this.urn = urn;
        this.locale = locale;
        this.authority = authority;
        this.title = title;
        this.description = description;
    }
}
