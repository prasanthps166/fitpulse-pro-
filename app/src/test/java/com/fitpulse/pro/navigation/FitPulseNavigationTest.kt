package com.fitpulse.pro.navigation

import com.fitpulse.pro.data.model.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FitPulseNavigationTest {

    @Test
    fun shouldShowBottomBar_hidesForFullscreenAndDialogRoutes() {
        assertFalse(shouldShowBottomBar(Screen.ExercisePicker.route))
        assertFalse(shouldShowBottomBar(Screen.BodyMeasurementLog.route))
        assertFalse(shouldShowBottomBar(Screen.WorkoutDetail.createRoute(42)))
    }

    @Test
    fun shouldShowBottomBar_showsForPrimaryDestinations() {
        assertTrue(shouldShowBottomBar(Screen.Home.route))
        assertTrue(shouldShowBottomBar(Screen.Progress.route))
    }

    @Test
    fun isBottomNavRouteSelected_mapsNestedRoutesToTheirTopLevelSection() {
        assertTrue(isBottomNavRouteSelected(Screen.Home, Screen.Settings.route))
        assertTrue(isBottomNavRouteSelected(Screen.Workouts, Screen.WorkoutDetail.createRoute(7)))
        assertTrue(isBottomNavRouteSelected(Screen.Learn, Screen.ArticleDetail.createRoute("mobility")))
        assertFalse(isBottomNavRouteSelected(Screen.Progress, Screen.Settings.route))
    }

    @Test
    fun resolveStartDestination_usesOnboardingUntilProfileIsCompleted() {
        assertEquals(Screen.Onboarding.route, resolveStartDestination(null))
        assertEquals(
            Screen.Onboarding.route,
            resolveStartDestination(UserProfile(hasCompletedOnboarding = false))
        )
        assertEquals(
            Screen.Home.route,
            resolveStartDestination(UserProfile(hasCompletedOnboarding = true))
        )
    }
}
