package com.example.fopsmart

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fopsmart.R
import com.example.fopsmart.ui.profile.ProfileActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(ProfileActivity::class.java)

    @Test
    fun testProfileActivity_BackButtonIsVisible() {
        onView(withId(R.id.backButtonProfile)).check(matches(isDisplayed()))
    }

    @Test
    fun testProfileActivity_ProfileCardIsVisible() {
        onView(withId(R.id.profileImage)).check(matches(isDisplayed()))
    }

    @Test
    fun testProfileActivity_NotificationsItemExists() {
        onView(withId(R.id.notificationsItem)).check(matches(isDisplayed()))
    }

    @Test
    fun testProfileActivity_PasswordItemExists() {
        onView(withId(R.id.passwordItem)).check(matches(isDisplayed()))
    }

    @Test
    fun testProfileActivity_LanguageItemExists() {
        onView(withId(R.id.languageItem)).check(matches(isDisplayed()))
    }

    @Test
    fun testProfileActivity_DarkThemeSwitchExists() {
        onView(withId(R.id.darkThemeSwitch)).check(matches(isDisplayed()))
    }

    @Test
    fun testProfileActivity_LogoutItemExists() {
        onView(withId(R.id.logoutItem)).check(matches(isDisplayed()))
    }
}