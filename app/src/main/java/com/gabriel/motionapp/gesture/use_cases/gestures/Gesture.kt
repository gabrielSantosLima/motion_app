package com.gabriel.motionapp.gesture.use_cases.gestures

import android.util.Log
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import kotlin.math.sqrt

abstract class Gesture(protected val id: GestureEnum) : IGesture {
    private var next: IGesture? = null

    companion object {
        const val TAG = "Gesture"
    }

    override fun setNext(gesture: IGesture): IGesture {
        Log.d(TAG, "Setting $id as next")
        next = gesture
        return gesture
    }

    protected fun getDistance(point1: NormalizedLandmark, point2: NormalizedLandmark): Float {
        val xDiff = point2.x() - point1.x()
        val yDiff = point2.y() - point1.y()
        return sqrt(xDiff * xDiff + yDiff * yDiff)
    }

    override fun isPerforming(handTrackingResult: HandLandmarkerResult): GestureEnum {
        if (next == null) {
            Log.d(TAG, "No one gesture detected")
            return GestureEnum.NONE
        }
        Log.d(TAG, "Next checking: if $id is performing")
        return next?.isPerforming(handTrackingResult) ?: GestureEnum.NONE
    }
}