package com.gabriel.motionapp.gesture.view_model

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.gabriel.motionapp.camera.services.CameraService

class GesturePanelViewModel : ViewModel() {
    companion object {
        const val TAG = "GesturePanelViewModel"
    }

    suspend fun bindToCamera(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        onReceiveImage: (image: Bitmap, rotation: Int) -> Unit
    ) {
        Log.d(TAG, "Binding to camera")
        val cameraService = CameraService.Builder(context, lifecycleOwner)
            .bindToCameraAnalyzer(onReceiveImage)
            .build()
        cameraService.bindToLifecycle()
    }
}