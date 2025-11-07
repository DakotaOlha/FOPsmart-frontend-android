package com.example.fopsmart

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fopsmart.DescActivity
import com.example.fopsmart.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DescActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(DescActivity::class.java)

    @Test
    fun testDescActivity_ViewPagerIsDisplayed() {
        onView(withId(R.id.viewPager)).check(matches(isDisplayed()))
    }

    @Test
    fun testDescActivity_IndicatorsAreDisplayed() {
        onView(withId(R.id.indicatorLayout)).check(matches(isDisplayed()))
    }

    @Test
    fun testDescActivity_DescriptionTextIsDisplayed() {
        onView(withId(R.id.textViewDesc)).check(matches(isDisplayed()))
    }

    @Test
    fun testDescActivity_ContinueButtonIsDisplayed() {
        onView(withId(R.id.button))
            .check(matches(isDisplayed()))
            .check(matches(withText("Далі")))
    }

    @Test
    fun testDescActivity_ContinueButtonClickable() {
        onView(withId(R.id.button)).check(matches(isEnabled()))
    }
}