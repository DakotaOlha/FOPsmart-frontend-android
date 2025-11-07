package com.example.fopsmart

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.fopsmart.MainActivity
import com.example.fopsmart.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testMainActivity_BottomNavigationIsVisible() {
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()))
    }

    @Test
    fun testMainActivity_FloatingActionButtonExists() {
        onView(withId(R.id.fab_add)).check(matches(isDisplayed()))
    }

    @Test
    fun testMainActivity_BottomAppBarExists() {
        onView(withId(R.id.bottom_app_bar)).check(matches(isDisplayed()))
    }
}