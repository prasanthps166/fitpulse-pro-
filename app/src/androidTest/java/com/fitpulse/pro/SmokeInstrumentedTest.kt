package com.fitpulse.pro

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fitpulse.pro.navigation.Screen
import com.fitpulse.pro.ui.TestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SmokeInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun launch_rendersOnboardingOrHome() {
        waitForPrimaryEntryScreen()

        if (hasNodeWithTag(TestTags.OnboardingScreen)) {
            composeRule.onNodeWithTag(TestTags.OnboardingScreen).assertIsDisplayed()
            composeRule.onNodeWithTag(TestTags.OnboardingSkipButton).assertIsDisplayed()
        } else {
            assertHomeContentVisible()
        }
    }

    @Test
    fun bottomNavigation_opensEachPrimaryTab_andHomeRendersAfterReturn() {
        skipOnboardingIfNeeded()
        assertHomeContentVisible()

        navigateToBottomTab(Screen.Workouts.route, TestTags.WorkoutScreen)
        navigateToBottomTab(Screen.Nutrition.route, TestTags.NutritionScreen)
        navigateToBottomTab(Screen.Progress.route, TestTags.ProgressScreen)
        navigateToBottomTab(Screen.Learn.route, TestTags.LearnScreen)
        navigateToBottomTab(Screen.Home.route, TestTags.HomeScreen)

        assertHomeContentVisible()
    }

    private fun skipOnboardingIfNeeded() {
        waitForPrimaryEntryScreen()
        if (hasNodeWithTag(TestTags.OnboardingScreen)) {
            composeRule.onNodeWithTag(TestTags.OnboardingSkipButton).performClick()
        }
        waitForTag(TestTags.HomeScreen)
    }

    private fun assertHomeContentVisible() {
        waitForTag(TestTags.HomeScreen)
        waitForTag(TestTags.HomeFocusCard)
        waitForTag(TestTags.HomeSummarySection)
        waitForTag(TestTags.HomeWaterCard)
        waitForTag(TestTags.HomeQuickStartSection)
        composeRule.onNodeWithTag(TestTags.HomeScreen).assertIsDisplayed()
    }

    private fun navigateToBottomTab(route: String, destinationTag: String) {
        composeRule.onNodeWithTag(TestTags.bottomNavItem(route)).performClick()
        waitForTag(destinationTag)
        composeRule.onNodeWithTag(destinationTag).assertIsDisplayed()
    }

    private fun waitForPrimaryEntryScreen() {
        composeRule.waitUntil(timeoutMillis = TIMEOUT_MILLIS) {
            hasNodeWithTag(TestTags.OnboardingScreen) || hasNodeWithTag(TestTags.HomeScreen)
        }
    }

    private fun waitForTag(tag: String) {
        composeRule.waitUntil(timeoutMillis = TIMEOUT_MILLIS) {
            hasNodeWithTag(tag)
        }
    }

    private fun hasNodeWithTag(tag: String): Boolean {
        return composeRule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
    }

    private companion object {
        const val TIMEOUT_MILLIS = 15_000L
    }
}
