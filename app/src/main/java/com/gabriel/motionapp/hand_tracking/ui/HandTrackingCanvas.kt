package com.gabriel.motionapp.hand_tracking.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

@Composable
fun HandTrackingCanvas(
    modifier: Modifier = Modifier,
    currentLandmarkResult: HandLandmarkerResult?,
    currentRotation: Int
) {
    Canvas(modifier = modifier) {
        currentLandmarkResult?.let { result ->
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