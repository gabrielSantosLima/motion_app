package com.gabriel.motionapp.core.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gabriel.motionapp.camera.ui.CameraDebuggerScreen
import com.gabriel.motionapp.gesture.ui.GestureDebuggerScreen

@Composable
fun NavigationStack() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.Home.route,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End) },
    ) {
        composable(route = Route.Home.route) {
            HomeScreen(navController)
        }
        composable(route = Route.CameraDebugger.route) {
            CameraDebuggerScreen(navController)
        }
        composable(route = Route.GestureDebugger.route) {
            GestureDebuggerScreen(navController)
        }
    }
}