package com.gabriel.motionapp.hand_tracking.use_cases

import android.util.Log
import com.gabriel.motionapp.hand_tracking.services.HandTrackingService
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

class ListenTrackingResultUseCase(val handTrackingService: HandTrackingService) {
    fun execute(
        onResultCallback: (result: HandLandmarkerResult) -> Unit
    ) {
        handTrackingService.registerOnHandDetectedListener { onResultCallback(it) }
        handTrackingService.registerOnErrorListener { error ->
            Log.e("ListenTrackingResultUseCase", "Error on detect hands: ${error.message}")
        }
    }
}