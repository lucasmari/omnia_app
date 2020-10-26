package com.lucas.omnia.models;

public class Body {

    private final String label;
    private final String name;
    private final String alias;
    private final String type;

    public Body(String label, String name, String alias, String type) {
        this.label = label;
        this.name = name;
        this.alias = alias;
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    public String getType() {
        return type;
    }
}
