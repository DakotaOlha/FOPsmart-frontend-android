package com.example.fopsmart

import com.example.fopsmart.R
import com.example.fopsmart.ui.login.LoginFormState
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LoginFormStateTest {
    @Test
    fun testLoginFormState_ValidData() {
        val state = LoginFormState(isDataValid = true)
        assertTrue(state.isDataValid)
        assertNull(state.usernameError)
        assertNull(state.passwordError)
    }

    @Test
    fun testLoginFormState_UsernameError() {
        val state = LoginFormState(usernameError = R.string.invalid_username)
        assertFalse(state.isDataValid)
        assertTrue(state.usernameError != null)
    }

    @Test
    fun testLoginFormState_PasswordError() {
        val state = LoginFormState(passwordError = R.string.invalid_password)
        assertFalse(state.isDataValid)
        assertTrue(state.passwordError != null)
    }
}