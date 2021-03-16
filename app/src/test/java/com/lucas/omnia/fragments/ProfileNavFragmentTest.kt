package com.lucas.omnia.fragments

import android.content.SharedPreferences
import com.lucas.omnia.models.User
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
    private lateinit var userEntry: User
    private lateinit var mockProfileNavFragment: ProfileNavFragment
    private lateinit var mockBrokenProfileNavFragment: ProfileNavFragment

    @Mock
    private lateinit var mockSharedPreferences: SharedPreferences
    @Mock
    private lateinit var mockBrokenSharedPreferences: SharedPreferences
    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor
    @Mock
    private lateinit var mockBrokenEditor: SharedPreferences.Editor

    @Before
    fun initMocks() {
        userEntry = User(UID, USERNAME)

        // Create a mocked SharedPreferences.
        mockProfileNavFragment = createMockSharedPreference()

        // Create a mocked SharedPreferences that fails at saving data.
        mockBrokenProfileNavFragment = createBrokenMockSharedPreference()
    }

    @Test
    fun profileNavFragment_SaveAndReadPersonalInformation() {
        // Save the personal information to SharedPreferences
        assertTrue(mockProfileNavFragment.saveUser(userEntry))

        // Read personal information from SharedPreferences
        val savedUser = mockProfileNavFragment.getUser()

        // Make sure both written and retrieved personal information are equal.
        assertEquals(
            userEntry.uid, savedUser.uid
        )
        assertEquals(
            userEntry.username, savedUser.username
        )
    }

    @Test
    fun profileNavFragment_SavePersonalInformationFailed_ReturnsFalse() {
        // Read personal information from a broken ProfileNavFragment
            assertFalse(
                mockBrokenProfileNavFragment.saveUser(userEntry)
            )
    }

    /**
     * Creates a mocked SharedPreferences.
     */
    private fun createMockSharedPreference(): ProfileNavFragment {
        // Mocking reading the SharedPreferences as if mockSharedPreferences was previously written
        // correctly.
        given(mockSharedPreferences.getString(eq(USER), anyString()))
            .willReturn(RETURN_VALUE)

        // Mocking a successful commit.
        given(mockEditor.commit()).willReturn(true)

        // Return the MockEditor when requesting it.
        given(mockSharedPreferences.edit()).willReturn(mockEditor)
        return ProfileNavFragment(mockSharedPreferences)
    }

    /**
     * Creates a mocked SharedPreferences that fails when writing.
     */
    private fun createBrokenMockSharedPreference(): ProfileNavFragment {
        // Mocking a commit that fails.
        given(mockBrokenEditor.commit()).willReturn(false)

        // Return the broken MockEditor when requesting it.
        given(mockBrokenSharedPreferences.edit()).willReturn(mockBrokenEditor)
        return ProfileNavFragment(mockBrokenSharedPreferences)
    }

    companion object {
        const val UID = "0"
        const val USERNAME = "test"
        const val USER = "User"
        const val RETURN_VALUE = """{"uid": "0", "username": "test"}"""
    }
}