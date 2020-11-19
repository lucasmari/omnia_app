package com.lucas.omnia.activities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthActivityTest {

    private final AuthActivity authActivity = new AuthActivity();

    @Test
    public void authActivity_ValidEmailSimple_ReturnsFalse() {
        assertFalse(authActivity.validateEmail("test@email.com"));
    }

    @Test
    public void authActivity_EmptyEmail_ReturnsTrue() {
        assertTrue(authActivity.validateEmail(""));
    }

    @Test
    public void authActivity_InvalidEmailNoTld_ReturnsTrue() {
        assertTrue(authActivity.validateEmail("test@email"));
    }

    @Test
    public void authActivity_InvalidEmailNoUsername_ReturnsTrue() {
        assertTrue(authActivity.validateEmail("@email.com"));
    }

    @Test
    public void authActivity_ValidPasswordShort_ReturnsFalse() {
        assertFalse(authActivity.validatePassword("123456"));
    }

    @Test
    public void authActivity_ValidPasswordLong() {
        assertFalse(authActivity.validatePassword("1234567890abcde"));
    }

    @Test
    public void authActivity_EmptyPassword_ReturnsTrue() {
        assertTrue(authActivity.validatePassword(""));
    }

    @Test
    public void authActivity_InvalidPasswordTooShort_ReturnsTrue() {
        assertTrue(authActivity.validatePassword("12345"));
    }

    @Test
    public void authActivity_InvalidPasswordTooLong() {
        assertTrue(authActivity.validatePassword("1234567890abcdef"));
    }

    @Test
    public void authActivity_Email_ReturnsUsername() {
        assertEquals(authActivity.usernameFromEmail("test@email.com"), "test");
    }
}