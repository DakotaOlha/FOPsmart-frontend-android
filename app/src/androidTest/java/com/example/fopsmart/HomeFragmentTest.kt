package com.example.fopsmart

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.fopsmart.MainActivity
import com.example.fopsmart.R
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testHomeFragment_IsDisplayed() {
        onView(withId(R.id.linearLayout2)).check(matches(isDisplayed()))
    }

    @Test
    fun testHomeFragment_BalanceTextIsVisible() {
        onView(withId(R.id.textView5)).check(matches(isDisplayed()))
    }

    @Test
    fun testHomeFragment_RecyclerViewIsVisible() {
        onView(withId(R.id.recyclerViewTransactions)).check(matches(isDisplayed()))
    }

    @Test
    fun testHomeFragment_ProfileButtonExists() {
        onView(withId(R.id.accountButton)).check(matches(isDisplayed()))
    }
}