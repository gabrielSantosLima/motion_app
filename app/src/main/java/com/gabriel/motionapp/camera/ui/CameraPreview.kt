package com.gabriel.motionapp.camera.ui

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.compose.CameraXViewfinder
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gabriel.motionapp.camera.view_model.CameraPreviewViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun CameraPreview() {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    if (cameraPermissionState.status.isGranted) {
        CameraPreviewContent()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
            verticalArrangement = Arrangement.spacedBy(
                space = 10.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "You don't have access to the camera. Click the button below to give access to your camera.",
                textAlign = TextAlign.Center
            )
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Open the camera")
            }
        }
    }
}

@Composable
fun CameraPreviewContent(viewModel: CameraPreviewViewModel = CameraPreviewViewModel()) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val TAG = "CameraPreviewContent"

    val surfaceRequest by viewModel.surfaceRequest.collectAsStateWithLifecycle()
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }

    fun onReceiveImage(image: Bitmap) {
        capturedImage = image
        Log.d(TAG, "Receiving image [width=${image.width}, height=${image.height}]")
    }

    LaunchedEffect(Unit) {
        viewModel.bindToCamera(context, lifecycleOwner) {
            onReceiveImage(it)
        }
    }

    surfaceRequest?.let { request ->
        CameraXViewfinder(surfaceRequest = request)
    }
}