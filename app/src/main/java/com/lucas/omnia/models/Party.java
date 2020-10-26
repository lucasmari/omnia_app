package com.lucas.omnia.models;

public class Party {

    private final String label;
    private final String name;

    public Party(String label, String name) {
        this.label = label;
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public String getName() {
        return name;
    }
}
