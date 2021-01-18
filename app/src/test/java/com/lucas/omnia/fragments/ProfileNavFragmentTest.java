package com.lucas.omnia.fragments;

import android.content.SharedPreferences;

import com.lucas.omnia.utils.SharedPreferenceEntry;
import com.lucas.omnia.utils.SharedPreferencesHelper;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class ProfileNavFragmentTest {
    private static final String UID = "0";

    private static final String USERNAME = "test";

    private SharedPreferenceEntry sharedPreferenceEntry;

    private SharedPreferencesHelper mockSharedPreferencesHelper;

    private SharedPreferencesHelper mockBrokenSharedPreferencesHelper;

    @Mock
    SharedPreferences mockSharedPreferences;

    @Mock
    SharedPreferences mockBrokenSharedPreferences;

    @Mock
    SharedPreferences.Editor mockEditor;

    @Mock
    SharedPreferences.Editor mockBrokenEditor;

    @Before
    public void initMocks() {
        sharedPreferenceEntry = new SharedPreferenceEntry(UID, USERNAME);

        // Create a mocked SharedPreferences.
        mockSharedPreferencesHelper = createMockSharedPreference();

        // Create a mocked SharedPreferences that fails at saving data.
        mockBrokenSharedPreferencesHelper = createBrokenMockSharedPreference();
    }

    @Test
    public void sharedPreferencesHelper_SaveAndReadPersonalInformation() {
        // Save the personal information to SharedPreferences
        boolean success = mockSharedPreferencesHelper.saveUserInfo(sharedPreferenceEntry);

        assertTrue(success, "Checking that SharedPreferenceEntry.save... returns true");

        // Read personal information from SharedPreferences
        SharedPreferenceEntry savedSharedPreferenceEntry =
                mockSharedPreferencesHelper.getUserInfo();

        // Make sure both written and retrieved personal information are equal.
        assertEquals("Checking that SharedPreferenceEntry.uid has been persisted and read " +
                        "correctly",
                sharedPreferenceEntry.getUid(), savedSharedPreferenceEntry.getUid());
        assertEquals("Checking that SharedPreferenceEntry.username has been persisted and read "
                        + "correctly",
                sharedPreferenceEntry.getUsername(), savedSharedPreferenceEntry.getUsername());
    }

    @Test
    public void sharedPreferencesHelper_SavePersonalInformationFailed_ReturnsFalse() {
        // Read personal information from a broken SharedPreferencesHelper
        boolean success =
                mockBrokenSharedPreferencesHelper.saveUserInfo(sharedPreferenceEntry);
        assertFalse(success, "Makes sure writing to a broken SharedPreferencesHelper returns false");
    }

    /**
     * Creates a mocked SharedPreferences.
     */
    private SharedPreferencesHelper createMockSharedPreference() {
        // Mocking reading the SharedPreferences as if mockSharedPreferences was previously written
        // correctly.
        when(mockSharedPreferences.getString(eq(SharedPreferencesHelper.KEY_ID), anyString()))
                .thenReturn(sharedPreferenceEntry.getUid());
        when(mockSharedPreferences.getString(eq(SharedPreferencesHelper.KEY_USERNAME), anyString()))
                .thenReturn(sharedPreferenceEntry.getUsername());

        // Mocking a successful commit.
        when(mockEditor.commit()).thenReturn(true);

        // Return the MockEditor when requesting it.
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);
        return new SharedPreferencesHelper(mockSharedPreferences);
    }

    /**
     * Creates a mocked SharedPreferences that fails when writing.
     */
    private SharedPreferencesHelper createBrokenMockSharedPreference() {
        // Mocking a commit that fails.
        when(mockBrokenEditor.commit()).thenReturn(false);

        // Return the broken MockEditor when requesting it.
        when(mockBrokenSharedPreferences.edit()).thenReturn(mockBrokenEditor);
        return new SharedPreferencesHelper(mockBrokenSharedPreferences);
    }
}