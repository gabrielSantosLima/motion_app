package com.gabriel.motionapp.camera.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gabriel.motionapp.R
import com.gabriel.motionapp.core.ui.components.Header
import com.gabriel.motionapp.hand_tracking.services.HandTrackingService

@Composable
fun CameraDebuggerScreen(navController: NavController) {
    val context = LocalContext.current
    val handTrackingService = HandTrackingService(context)

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        CameraPreview(handTrackingService)
        Header(
            modifier = Modifier
                .background(Color.Transparent)
                .padding(vertical = 40.dp, horizontal = 20.dp)
        ) {
            Button(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color.Transparent,
                        shape = RoundedCornerShape(50.dp)
                    )
                    .width(50.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                onClick = { navController.popBackStack() }
            ) {
                Icon(
                    painter = painterResource(R.drawable.chevron_backward_24px),
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
        }
    }
}
