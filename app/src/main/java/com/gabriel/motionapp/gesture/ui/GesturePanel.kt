package com.gabriel.motionapp.gesture.ui

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.gabriel.motionapp.gesture.use_cases.DetectGestureUseCase
import com.gabriel.motionapp.gesture.view_model.GesturePanelViewModel
import com.gabriel.motionapp.hand_tracking.services.HandTrackingService
import com.gabriel.motionapp.hand_tracking.ui.HandTrackingCanvas
import com.gabriel.motionapp.hand_tracking.use_cases.DetectHandUseCase
import com.gabriel.motionapp.hand_tracking.use_cases.ListenTrackingResultUseCase
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

val detectGestureUseCase = DetectGestureUseCase()

@Composable
fun GesturePanel(
    viewModel: GesturePanelViewModel = GesturePanelViewModel(),
    handTrackingService: HandTrackingService
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val tag = "GesturePanel"

    // Use Cases
    val detectHandUseCase = DetectHandUseCase(handTrackingService)
    val listenTrackingResultUseCase = ListenTrackingResultUseCase(handTrackingService)

    var currentLandmarks by remember { mutableStateOf<HandLandmarkerResult?>(null) }
    var currentRotation by remember { mutableIntStateOf(0) }

    fun onReceiveImage(image: Bitmap, rotation: Int) {
        currentRotation = rotation
        detectHandUseCase.execute(image, rotation)
    }

    fun detectGesture(result: HandLandmarkerResult) {
        currentLandmarks = result
        val gesture = detectGestureUseCase.execute(result)
    }

    LaunchedEffect(Unit) {
        listenTrackingResultUseCase.execute { detectGesture(it) }
        viewModel.bindToCamera(context, lifecycleOwner) { bitmap, rotation ->
            onReceiveImage(bitmap, rotation)
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HandTrackingCanvas(
            modifier = Modifier
                .padding(16.dp)
                .size(300.dp)
                .background(Color.Transparent),
            currentLandmarkResult = currentLandmarks,
            currentRotation = currentRotation
        )
    }
}