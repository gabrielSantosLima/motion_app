package com.gabriel.motionapp.gesture.ui

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.gabriel.motionapp.gesture.use_cases.DetectGestureUseCase
import com.gabriel.motionapp.gesture.use_cases.TriggerActionOnGestureUseCase
import com.gabriel.motionapp.gesture.use_cases.gestures.GestureEnum
import com.gabriel.motionapp.gesture.view_model.GesturePanelViewModel
import com.gabriel.motionapp.hand_tracking.services.HandTrackingService
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

    // Set interval to gesture detection
    var lastExecutionTime by remember { mutableLongStateOf(0L) }
    val minIntervals = 500L

    // Use Cases
    val detectHandUseCase = DetectHandUseCase(handTrackingService)
    val listenTrackingResultUseCase = ListenTrackingResultUseCase(handTrackingService)
    val triggerActionOnGestureUseCase = TriggerActionOnGestureUseCase(3)
        .registerAction(GestureEnum.PALM_UP) {
            Log.d(tag, "Sending file through BLE for the nearest device")
        }
        .registerAction(GestureEnum.FIST) {
            Log.d(tag, "Listening to the nearest device to receive the file")
        }

    fun onReceiveImage(image: Bitmap, rotation: Int) {
        detectHandUseCase.execute(image, rotation)
    }

    fun detectGesture(result: HandLandmarkerResult) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastExecutionTime < minIntervals) {
            return
        }
        lastExecutionTime = currentTime
        val gesture = detectGestureUseCase.execute(result)
        Log.d(tag, "Detected gesture: ${gesture.name}")

        // Trigger action on gesture
        triggerActionOnGestureUseCase.execute(gesture)
    }

    LaunchedEffect(Unit) {
        listenTrackingResultUseCase.execute { detectGesture(it) }
        viewModel.bindToCamera(context, lifecycleOwner) { bitmap, rotation ->
            onReceiveImage(bitmap, rotation)
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

    }
}