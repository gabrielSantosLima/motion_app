package com.gabriel.motionapp.gesture.ui

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
import kotlinx.coroutines.launch

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
    val scope = rememberCoroutineScope()
    val rippleRadius = remember { Animatable(0f) }
    val rippleAlpha = remember { Animatable(0f) }
    var rippleColor by remember { mutableStateOf(Color.Cyan) }
    var status by remember { mutableStateOf("Idle") }


    // Function to trigger the animation
    fun startRippleAnimation() {
        scope.launch {
            // Reset
            rippleRadius.snapTo(0f)
            rippleAlpha.snapTo(0.8f)

            // Animate: Radius expands while alpha fades
            launch {
                rippleRadius.animateTo(
                    targetValue = 400f,
                    animationSpec = tween(durationMillis = 1800)
                )
            }
            launch {
                rippleAlpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 1800)
                )
            }
        }
    }

    // Use Cases
    val detectHandUseCase = DetectHandUseCase(handTrackingService)
    val listenTrackingResultUseCase = ListenTrackingResultUseCase(handTrackingService)
    val triggerActionOnGestureUseCase = TriggerActionOnGestureUseCase(3)
        .registerAction(GestureEnum.PALM_UP) {
            startRippleAnimation()
            rippleColor = Color.Cyan
            status = "Palm Up"
        }
        .registerAction(GestureEnum.FIST) {
            startRippleAnimation()
            rippleColor = Color.Red
            status = "Fist"
        }.registerAction(GestureEnum.NONE) {
            status = "Idle"
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

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Text(status, modifier = Modifier.align(Alignment.Center))
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        ) {
            drawCircle(
                color = rippleColor.copy(alpha = rippleAlpha.value),
                radius = rippleRadius.value,
                center = center,
                style = Stroke(width = 8f)
            )
            // Optional: Second ring for a richer effect
            drawCircle(
                color = rippleColor.copy(alpha = rippleAlpha.value / 2),
                radius = rippleRadius.value * 0.7f,
                center = center,
                style = Stroke(width = 4f)
            )
        }
    }
}