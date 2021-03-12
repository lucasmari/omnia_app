package com.lucas.omnia.activities

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class SearchLawActivityTest {
    private val searchLawActivity = SearchLawActivity()
    @Test
    fun searchLawActivity_ValidQuerySimple_ReturnsUrl() {
        Assertions.assertEquals(
            searchLawActivity.composeQuery("data"), "https://www.lexml.gov" +
                    ".br/busca/SRU?query=urn=lei&query=description=\"data\""
        )
    }

    @Test
    fun searchLawActivity_ValidQueryComposed_ReturnsUrl() {
        Assertions.assertEquals(
            searchLawActivity.composeQuery("data law"), "https://www.lexml.gov" +
                    ".br/busca/SRU?query=urn=lei&query=description=\"data+law\""
        )
    }
}