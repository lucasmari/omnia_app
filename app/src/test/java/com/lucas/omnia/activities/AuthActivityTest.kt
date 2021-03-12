package com.lucas.omnia.activities

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class AuthActivityTest {
    private val authActivity = AuthActivity()
    @Test
    fun authActivity_ValidEmailSimple_ReturnsFalse() {
        Assertions.assertFalse(authActivity.validateEmail("test@email.com"))
    }

    @Test
    fun authActivity_EmptyEmail_ReturnsTrue() {
        Assertions.assertTrue(authActivity.validateEmail(""))
    }

    @Test
    fun authActivity_InvalidEmailNoTld_ReturnsTrue() {
        Assertions.assertTrue(authActivity.validateEmail("test@email"))
    }

    @Test
    fun authActivity_InvalidEmailNoUsername_ReturnsTrue() {
        Assertions.assertTrue(authActivity.validateEmail("@email.com"))
    }

    @Test
    fun authActivity_ValidPasswordShort_ReturnsFalse() {
        Assertions.assertFalse(authActivity.validatePassword("123456"))
    }

    @Test
    fun authActivity_ValidPasswordLong() {
        Assertions.assertFalse(authActivity.validatePassword("1234567890abcde"))
    }

    @Test
    fun authActivity_EmptyPassword_ReturnsTrue() {
        Assertions.assertTrue(authActivity.validatePassword(""))
    }

    @Test
    fun authActivity_InvalidPasswordTooShort_ReturnsTrue() {
        Assertions.assertTrue(authActivity.validatePassword("12345"))
    }

    @Test
    fun authActivity_InvalidPasswordTooLong() {
        Assertions.assertTrue(authActivity.validatePassword("1234567890abcdef"))
    }

    @Test
    fun authActivity_Email_ReturnsUsername() {
        Assertions.assertEquals(authActivity.usernameFromEmail("test@email.com"), "test")
    }
}