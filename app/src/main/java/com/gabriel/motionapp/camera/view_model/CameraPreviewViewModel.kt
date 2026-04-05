package com.gabriel.motionapp.camera.view_model

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.SurfaceRequest
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.gabriel.motionapp.camera.services.CameraService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CameraPreviewViewModel : ViewModel() {
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest = _surfaceRequest.asStateFlow()

    companion object {
        const val TAG = "CameraPreviewViewModel"
    }

    suspend fun bindToCamera(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        onReceiveImage: (image: Bitmap, rotation: Int) -> Unit
    ) {
        Log.d(TAG, "Binding to camera")
        val cameraService = CameraService.Builder(context, lifecycleOwner)
            .bindToCameraAnalyzer(onReceiveImage)
            .bindToCameraPreview { newRequest ->
                Log.d(TAG, "Receiving a new surface provider")
                _surfaceRequest.update { newRequest }
            }
            .build()
        cameraService.bindToLifecycle()
    }
}