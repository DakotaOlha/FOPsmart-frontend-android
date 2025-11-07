package com.example.fopsmart

import com.example.fopsmart.ui.login.LoggedInUserView
import org.junit.Test
import kotlin.test.assertEquals

class LoggedInUserViewTest {
    @Test
    fun testLoggedInUserView() {
        val userView = LoggedInUserView(displayName = "Alice Smith")
        assertEquals("Alice Smith", userView.displayName)
    }

    @Test
    fun testLoggedInUserView_DifferentNames() {
        val users = listOf(
            LoggedInUserView("User1"),
            LoggedInUserView("User2"),
            LoggedInUserView("User3")
        )
        assertEquals(3, users.size)
        assertEquals("User2", users[1].displayName)
    }
}