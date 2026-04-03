package com.gabriel.motionapp.core.ui

sealed class Route(val route: String) {
    object Home: Route("home_screen")
    object CameraDebugger: Route("camera_debugger_screen")
}