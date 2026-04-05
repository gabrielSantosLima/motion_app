package com.gabriel.motionapp.camera.services

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.withContext

class CameraService(
    val context: Context,
    val lifecycleOwner: LifecycleOwner,
    val useCases: Array<UseCase>,
    val cameraSelector: CameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
) {
    internal class Builder(val context: Context, val lifecycleOwner: LifecycleOwner) {
        private val useCases = mutableListOf<UseCase>()

        companion object {
            const val TAG = "CameraService"
        }

        fun bindToCameraAnalyzer(
            onReceiveImage: (image: Bitmap, rotation: Int) -> Unit
        ): Builder {
            Log.d(TAG, "Registering a new image analyzer")
            val imageAnalysisUseCase = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            imageAnalysisUseCase.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                onReceiveImage(imageProxy.toBitmap(), imageProxy.imageInfo.rotationDegrees)
                imageProxy.close()
            }
            useCases.add(imageAnalysisUseCase)
            return this@Builder
        }

        fun bindToCameraPreview(onRequestSurface: (surface: SurfaceRequest) -> Unit): Builder {
            Log.d(TAG, "Registering a new camera preview")
            val cameraPreviewUseCase = Preview.Builder().build().apply {
                setSurfaceProvider { onRequestSurface(it) }
            }
            useCases.add(cameraPreviewUseCase)
            return this@Builder
        }

        fun build(): CameraService {
            return CameraService(context, lifecycleOwner, useCases.toTypedArray())
        }
    }

    suspend fun bindToLifecycle() {
        withContext(Dispatchers.Main) {
            val processCameraProvider = ProcessCameraProvider.awaitInstance(context)
            try {
                processCameraProvider.unbindAll()
                processCameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    *useCases
                )
            } catch (e: Exception) {
                Log.e("CameraService", "Use case binding failed", e)
            }
        }


        try {
            awaitCancellation()
        } finally {
            withContext(NonCancellable) {
                val processCameraProvider = ProcessCameraProvider.awaitInstance(context)
                processCameraProvider.unbindAll()
            }
        }
    }
}

