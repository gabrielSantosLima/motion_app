package com.gabriel.motionapp.gesture.use_cases.gestures

import android.util.Log
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

class PalmUpGesture : Gesture(GestureEnum.PALM_UP) {
    companion object {
        const val PALM_UP_THRESHOLD = 0.15
        const val RATIO_THRESHOLD = 0.03
    }

    override fun isPerforming(handTrackingResult: HandLandmarkerResult): GestureEnum {
        if (reachTheDistanceLimit(handTrackingResult)) {
            return GestureEnum.NONE
        }
        if (handIsExtended(handTrackingResult)) {
            return GestureEnum.PALM_UP
        }
        return GestureEnum.FIST
    }

    private fun reachTheDistanceLimit(handTrackingResult: HandLandmarkerResult): Boolean {
        val normalizedLandmarks = handTrackingResult.landmarks()[0]
        val ratio = normalizedLandmarks[9].z()
        return ratio >= RATIO_THRESHOLD
    }

    private fun handIsExtended(handTrackingResult: HandLandmarkerResult): Boolean {
        val normalizedLandmarks = handTrackingResult.landmarks()[0]

        // Tips
        val thumpTip = normalizedLandmarks[4]
        val indexFingerTip = normalizedLandmarks[8]
        val middleFingerTip = normalizedLandmarks[12]
        val ringFingerTip = normalizedLandmarks[16]
        val pinkyFingerTip = normalizedLandmarks[20]

        // MCP
        val thumbMCP = normalizedLandmarks[2]
        val indexFingerMCP = normalizedLandmarks[5]
        val middleFingerMCP = normalizedLandmarks[9]
        val ringFingerMCP = normalizedLandmarks[13]
        val pinkyMCP = normalizedLandmarks[17]

        // Calculate the distance between Tip and MCP
        val thumpDistance = getDistance(thumpTip, thumbMCP)
        val indexFingerDistance = getDistance(indexFingerTip, indexFingerMCP)
        val middleFingerDistance = getDistance(middleFingerTip, middleFingerMCP)
        val ringFingerDistance = getDistance(ringFingerTip, ringFingerMCP)
        val pinkyDistance = getDistance(pinkyFingerTip, pinkyMCP)

        // Correlate distance to detect palm up gesture according a threshold
        val ratio = middleFingerMCP.z()
        val adaptedThreshold = PALM_UP_THRESHOLD - ratio

        Log.d(TAG, "${id.name} -- AdaptedThreshold: $adaptedThreshold")
        Log.d(TAG, "${id.name} -- Ratio: $ratio")
        Log.d(TAG, "${id.name} -- Thumb Distance: $thumpDistance")
        Log.d(TAG, "${id.name} -- Index Distance: $indexFingerDistance")
        Log.d(TAG, "${id.name} -- Middle Distance: $middleFingerDistance")
        Log.d(TAG, "${id.name} -- Ring Distance: $ringFingerDistance")
        Log.d(TAG, "${id.name} -- Pinky Distance: $pinkyDistance")

        val fingersExtended = listOf(
            thumpDistance, indexFingerDistance, middleFingerDistance,
            ringFingerDistance, pinkyDistance
        ).count { it > adaptedThreshold }
        Log.d(TAG, "${id.name} -- Fingers Extended: $fingersExtended")
        return fingersExtended >= 4
    }
}