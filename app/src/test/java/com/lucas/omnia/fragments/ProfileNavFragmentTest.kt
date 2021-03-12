package com.lucas.omnia.fragments

import android.content.SharedPreferences
import com.lucas.omnia.utils.SharedPreferenceEntry
import com.lucas.omnia.utils.SharedPreferencesHelper
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ProfileNavFragmentTest {
    private lateinit var sharedPreferenceEntry: SharedPreferenceEntry
    private lateinit var mockSharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var mockBrokenSharedPreferencesHelper: SharedPreferencesHelper

    @Mock private lateinit var mockSharedPreferences: SharedPreferences
    @Mock private lateinit var mockBrokenSharedPreferences: SharedPreferences
    @Mock private lateinit var mockEditor: SharedPreferences.Editor
    @Mock private lateinit var mockBrokenEditor: SharedPreferences.Editor

    @Before fun initMocks() {
        sharedPreferenceEntry = SharedPreferenceEntry(UID, USERNAME)

        // Create a mocked SharedPreferences.
        mockSharedPreferencesHelper = createMockSharedPreference()

        // Create a mocked SharedPreferences that fails at saving data.
        mockBrokenSharedPreferencesHelper = createBrokenMockSharedPreference()
    }

    @Test fun sharedPreferencesHelper_SaveAndReadPersonalInformation() {
        // Save the personal information to SharedPreferences
        val success = mockSharedPreferencesHelper.savePersonalInfo(sharedPreferenceEntry)
        assertTrue(success, "Checking that SharedPreferenceEntry.save... returns true")

        // Read personal information from SharedPreferences
        val savedSharedPreferenceEntry = mockSharedPreferencesHelper.getPersonalInfo()

        // Make sure both written and retrieved personal information are equal.
        assertEquals(
            "0",
            sharedPreferenceEntry.uid, savedSharedPreferenceEntry.uid
        )
        assertEquals(
            "test",
            sharedPreferenceEntry.username, savedSharedPreferenceEntry.username
        )
    }

    @Test fun sharedPreferencesHelper_SavePersonalInformationFailed_ReturnsFalse() {
        // Read personal information from a broken SharedPreferencesHelper
        val success = mockBrokenSharedPreferencesHelper.savePersonalInfo(sharedPreferenceEntry)
        assertFalse(
            success,
            "Makes sure writing to a broken SharedPreferencesHelper returns false"
        )
    }

    /**
     * Creates a mocked SharedPreferences.
     */
    private fun createMockSharedPreference(): SharedPreferencesHelper {
        // Mocking reading the SharedPreferences as if mockSharedPreferences was previously written
        // correctly.
        given(mockSharedPreferences.getString(eq(SharedPreferencesHelper.KEY_ID), anyString()))
            .willReturn(sharedPreferenceEntry.uid)
        given(mockSharedPreferences.getString(eq(SharedPreferencesHelper.KEY_USERNAME), anyString()))
            .willReturn(sharedPreferenceEntry.username)

        // Mocking a successful commit.
        given(mockEditor.commit()).willReturn(true)

        // Return the MockEditor when requesting it.
        given(mockSharedPreferences.edit()).willReturn(mockEditor)
        return SharedPreferencesHelper(mockSharedPreferences)
    }

    /**
     * Creates a mocked SharedPreferences that fails when writing.
     */
    private fun createBrokenMockSharedPreference(): SharedPreferencesHelper {
        // Mocking a commit that fails.
        given(mockBrokenEditor.commit()).willReturn(false)

        // Return the broken MockEditor when requesting it.
        given(mockBrokenSharedPreferences.edit()).willReturn(mockBrokenEditor)
        return SharedPreferencesHelper(mockBrokenSharedPreferences)
    }

    companion object {
        private const val UID = "0"
        private const val USERNAME = "test"
    }
}