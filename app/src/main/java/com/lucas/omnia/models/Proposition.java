package com.lucas.omnia.models;

public class Proposition {

    private final String label;
    private final String year;
    private final String summary;

    public Proposition(String label, String year, String summary) {
        this.label = label;
        this.year = year;
        this.summary = summary;
    }

    public String getLabel() {
        return label;
    }

    public String getYear() {
        return year;
    }

    public String getSummary() {
        return summary;
    }
}
