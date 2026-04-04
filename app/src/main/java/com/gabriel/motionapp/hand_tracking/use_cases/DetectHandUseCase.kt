package com.gabriel.motionapp.hand_tracking.use_cases

import android.graphics.Bitmap
import com.gabriel.motionapp.hand_tracking.services.HandTrackingService

class DetectHandUseCase(private val handTrackingService: HandTrackingService) {
    fun execute(image: Bitmap, rotation: Int = 0) {
        handTrackingService.detect(image, rotation)
    }
}