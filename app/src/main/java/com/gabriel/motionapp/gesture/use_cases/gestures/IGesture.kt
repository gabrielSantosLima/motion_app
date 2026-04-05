package com.gabriel.motionapp.gesture.use_cases.gestures

import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

interface IGesture {
    fun isPerforming(handTrackingResult: HandLandmarkerResult): GestureEnum
    fun setNext(gesture: IGesture): IGesture
}