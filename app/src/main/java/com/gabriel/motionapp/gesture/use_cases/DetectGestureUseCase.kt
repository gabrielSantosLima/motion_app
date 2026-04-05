package com.gabriel.motionapp.gesture.use_cases

import android.util.Log
import com.gabriel.motionapp.gesture.use_cases.gestures.GestureEnum
import com.gabriel.motionapp.gesture.use_cases.gestures.PalmUpGesture
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

class DetectGestureUseCase() {
    private val palmUpGesture = PalmUpGesture()
    private var lastExecutionTime = 0L

    companion object {
        const val TAG = "DetectGestureUseCase"
    }

    fun handsExists(handTrackingResult: HandLandmarkerResult): Boolean {
        if (handTrackingResult.landmarks().isEmpty()) return false
        Log.d(TAG, "Checking landmarks [size=${handTrackingResult.landmarks()[0].size}]")
        return handTrackingResult.landmarks()[0].size == 21
    }

    fun execute(handTrackingResult: HandLandmarkerResult, minIntervals: Long = 1000L): GestureEnum {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastExecutionTime < minIntervals) {
            return GestureEnum.NONE
        }
        lastExecutionTime = currentTime
        if (!handsExists(handTrackingResult)) {
            return GestureEnum.NONE
        }
        val gesture = palmUpGesture.isPerforming(handTrackingResult)
        Log.d(TAG, "Gesture detected as ${gesture.name}")
        return gesture
    }
}