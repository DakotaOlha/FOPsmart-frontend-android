package com.example.fopsmart

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fopsmart.R
import com.example.fopsmart.ui.login.LoginActivity
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun testLoginActivity_IsVisible() {
        onView(withId(R.id.username)).check(matches(isDisplayed()))
        onView(withId(R.id.password)).check(matches(isDisplayed()))
        onView(withId(R.id.login)).check(matches(isDisplayed()))
    }

    @Test
    fun testLoginActivity_EnterEmail() {
        onView(withId(R.id.username))
            .perform(typeText("test@example.com"))
            .check(matches(withText("test@example.com")))
    }

    @Test
    fun testLoginActivity_EnterPassword() {
        onView(withId(R.id.password))
            .perform(typeText("password123"))
            .check(matches(withText("password123")))
    }

    @Test
    fun testLoginActivity_GoToRegister() {
        onView(withId(R.id.tvGoToLogin))
            .check(matches(isDisplayed()))
            .check(matches(withText("Зареєструватися")))
    }
}