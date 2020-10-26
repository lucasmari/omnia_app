package com.lucas.omnia.models;

public class Poll {

    private final String date;
    private final String body;
    private final String description;

    public Poll(String date, String body, String description) {
        this.date = date;
        this.body = body;
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public String getBody() {
        return body;
    }

    public String getDescription() {
        return description;
    }
}
