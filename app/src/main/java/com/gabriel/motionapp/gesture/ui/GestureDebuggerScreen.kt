package com.gabriel.motionapp.gesture.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.gabriel.motionapp.camera.ui.CameraPermissionHandler
import com.gabriel.motionapp.core.ui.components.DebugHeader
import com.gabriel.motionapp.hand_tracking.services.HandTrackingService

@Composable
fun GestureDebuggerScreen(navController: NavController) {
    val context = LocalContext.current
    val handTrackingService = HandTrackingService(context)

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        CameraPermissionHandler {
            GesturePanel(handTrackingService = handTrackingService)
        }
        DebugHeader(navController)
    }
}