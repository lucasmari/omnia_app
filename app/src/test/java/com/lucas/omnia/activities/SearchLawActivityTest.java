package com.lucas.omnia.activities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SearchLawActivityTest {
    private final SearchLawActivity searchLawActivity = new SearchLawActivity();

    @Test
    public void searchLawActivity_ValidQuerySimple_ReturnsUrl() {
        assertEquals(searchLawActivity.composeQuery("data"), "https://www.lexml.gov" +
                ".br/busca/SRU?query=urn=lei&query=description=\"data\"");
    }

    @Test
    public void searchLawActivity_ValidQueryComposed_ReturnsUrl() {
        assertEquals(searchLawActivity.composeQuery("data law"), "https://www.lexml.gov" +
                ".br/busca/SRU?query=urn=lei&query=description=\"data+law\"");
    }
}