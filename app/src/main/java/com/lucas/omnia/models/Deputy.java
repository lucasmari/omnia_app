package com.lucas.omnia.models;

public class Deputy {

    private final String name;
    private final String party;
    private final String state;
    private final String email;

    public Deputy(String name, String party, String state, String email) {
        this.name = name;
        this.party = party;
        this.state = state;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getParty() {
        return party;
    }

    public String getState() {
        return state;
    }

    public String getEmail() {
        return email;
    }
}
