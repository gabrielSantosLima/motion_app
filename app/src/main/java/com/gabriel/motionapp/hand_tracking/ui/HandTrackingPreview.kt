package com.gabriel.motionapp.hand_tracking.ui

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.compose.CameraXViewfinder
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gabriel.motionapp.camera.view_model.CameraPreviewViewModel
import com.gabriel.motionapp.hand_tracking.services.HandTrackingService
import com.gabriel.motionapp.hand_tracking.use_cases.DetectHandUseCase
import com.gabriel.motionapp.hand_tracking.use_cases.ListenTrackingResultUseCase
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

@Composable
fun HandTrackingPreview(
    viewModel: CameraPreviewViewModel = CameraPreviewViewModel(),
    handTrackingService: HandTrackingService
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val tag = "CameraPreviewContent"

    // States
    var lastLandMarkResult by remember { mutableStateOf<HandLandmarkerResult?>(null) }
    var currentRotation by remember { mutableIntStateOf(0) }

    // Use Cases
    val detectHandUseCase = DetectHandUseCase(handTrackingService)
    val listenTrackingResultUseCase = ListenTrackingResultUseCase(handTrackingService)
    val surfaceRequest by viewModel.surfaceRequest.collectAsStateWithLifecycle()

    fun onReceiveImage(image: Bitmap, rotation: Int) {
        Log.d(tag, "Received image [w=${image.width}, h=${image.height}]")
        currentRotation = rotation
        detectHandUseCase.execute(image, rotation)
    }

    LaunchedEffect(Unit) {
        listenTrackingResultUseCase.execute { result ->
            lastLandMarkResult = result
        }
        viewModel.bindToCamera(context, lifecycleOwner) { bitmap, rotation ->
            onReceiveImage(bitmap, rotation)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        surfaceRequest?.let { request ->
            CameraXViewfinder(
                surfaceRequest = request,
                modifier = Modifier.fillMaxSize()
            )
        }

        Canvas(
            modifier = Modifier
                .padding(16.dp)
                .size(200.dp)
                .align(Alignment.TopEnd)
                .background(Color.Transparent)
        ) {
            lastLandMarkResult?.let { result ->
                for (landmark in result.landmarks()) {

                    // Manual mapping logic to handle 90/270 degree inversion
                    fun mapPoint(x: Float, y: Float): Offset {
                        return when (currentRotation) {
                            90 -> Offset((1 - y) * size.width, x * size.height)
                            270 -> Offset(y * size.width, (1 - x) * size.height)
                            180 -> Offset((1 - x) * size.width, (1 - y) * size.height)
                            else -> Offset(x * size.width, y * size.height)
                        }
                    }

                    // Draw Connections
                    HandLandmarker.HAND_CONNECTIONS.forEach { connection ->
                        val start = landmark[connection!!.start()]
                        val end = landmark[connection.end()]

                        drawLine(
                            color = Color.Red,
                            start = mapPoint(start.x(), start.y()),
                            end = mapPoint(end.x(), end.y()),
                            strokeWidth = 2f
                        )
                    }

                    // Draw Landmarks
                    for (pt in landmark) {
                        drawCircle(
                            color = Color.Cyan,
                            radius = 3f,
                            center = mapPoint(pt.x(), pt.y())
                        )
                    }
                }
            }
        }
    }
}