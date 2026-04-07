package com.gabriel.motionapp.hand_tracking.services

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.ImageProcessingOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

class HandTrackingService(val context: Context) {
    private lateinit var handLandmarker: HandLandmarker
    private var onHandDetectedListener: ((result: HandLandmarkerResult) -> Unit)? = null
    private var onErrorListener: ((error: RuntimeException) -> Unit)? = null
    private var isReady = false

    companion object {
        const val TAG = "HandTrackingService"
        const val MP_HAND_LANDMARKER_TASK = "hand_landmarker.task"
    }

    init {
        bindHandLandmarker()
    }

    fun bindHandLandmarker() {
        Log.d(TAG, "Initializing hand tracking module")
        try {
            val baseOptions =
                BaseOptions.builder()
                    .setModelAssetPath(MP_HAND_LANDMARKER_TASK)
                    .setDelegate(Delegate.GPU)
                    .build()
            val optionsBuilder =
                HandLandmarker.HandLandmarkerOptions.builder()
                    .setBaseOptions(baseOptions)
                    .setResultListener(this::onHandDetected)
                    .setErrorListener(this::onError)
                    .setRunningMode(RunningMode.LIVE_STREAM)
            val options = optionsBuilder.build()
            handLandmarker =
                HandLandmarker.createFromOptions(context, options)
            Log.d(TAG, "Hand tracking ready")
            isReady = true
        } catch (error: Exception) {
            isReady = false
            Log.e(TAG, "Error on initialize Hand Landmarker -- ${error.message}")
        }
    }

    fun onHandDetected(
        result: HandLandmarkerResult,
        input: MPImage
    ) {
        Log.d(TAG, "onHandDetected [w=${input.width}, h=${input.height}]")
        onHandDetectedListener?.invoke(result)
    }

    fun onError(error: RuntimeException) {
        Log.e(TAG, "Error on detect hands: ${error.message}")
        onErrorListener?.invoke(error)
    }

    fun registerOnHandDetectedListener(callback: (result: HandLandmarkerResult) -> Unit) {
        onHandDetectedListener = callback
    }

    fun registerOnErrorListener(callback: (error: RuntimeException) -> Unit) {
        onErrorListener = callback
    }

    fun detect(image: Bitmap, rotation: Int = 0) {
        if (!isReady) {
            Log.w(TAG, "Hand tracking service not initialized correctly")
            return
        }
        val mpImage = BitmapImageBuilder(image).build()
        val imageProcessingOptions = ImageProcessingOptions.builder()
            .setRotationDegrees(rotation)
            .build()
        val frameTime = SystemClock.uptimeMillis()
        // Trigger onHandDetected/onError callbacks
        handLandmarker.detectAsync(mpImage, imageProcessingOptions, frameTime)
    }
}