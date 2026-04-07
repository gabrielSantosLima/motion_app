package com.gabriel.motionapp.hand_tracking.ui

import android.graphics.Bitmap
import androidx.camera.compose.CameraXViewfinder
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gabriel.motionapp.camera.view_model.CameraPreviewViewModel
import com.gabriel.motionapp.hand_tracking.services.HandTrackingService
import com.gabriel.motionapp.hand_tracking.use_cases.DetectHandUseCase

@Composable
fun HandTrackingPreview(
    viewModel: CameraPreviewViewModel = CameraPreviewViewModel(),
    handTrackingService: HandTrackingService
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    // States
    val surfaceRequest by viewModel.surfaceRequest.collectAsStateWithLifecycle()

    fun onReceiveImage(image: Bitmap, rotation: Int) {
        DetectHandUseCase(handTrackingService).execute(image, rotation)
    }

    LaunchedEffect(Unit) {
        viewModel.bindToCamera(context, lifecycleOwner) { bitmap, rotation ->
            onReceiveImage(bitmap, rotation)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        surfaceRequest?.let { request ->
            CameraXViewfinder(
                surfaceRequest = request,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}