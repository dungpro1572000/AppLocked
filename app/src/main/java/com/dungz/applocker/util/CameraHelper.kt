package com.dungz.applocker.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class CameraHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend fun takePhoto(lifecycleOwner: LifecycleOwner): Uri? {
        return suspendCancellableCoroutine { continuation ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                
                val imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        imageCapture
                    )

                    val photoFile = createImageFile()
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                    imageCapture.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                                saveToGallery(savedUri)
                                continuation.resume(savedUri)
                            }

                            override fun onError(exception: ImageCaptureException) {
                                continuation.resume(null)
                            }
                        }
                    )
                } catch (e: Exception) {
                    continuation.resume(null)
                }
            }, ContextCompat.getMainExecutor(context))
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(null)
        return File.createTempFile("FAILED_ATTEMPT_$timeStamp", ".jpg", storageDir)
    }

    private fun saveToGallery(uri: Uri) {
        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "Failed_Login_Attempt_${System.currentTimeMillis()}")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/AppLocker")
                }
            }

            val resolver = context.contentResolver
            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            
            imageUri?.let { galleryUri ->
                resolver.openInputStream(uri)?.use { input ->
                    resolver.openOutputStream(galleryUri)?.use { output ->
                        input.copyTo(output)
                    }
                }
            }
        } catch (e: Exception) {
            // Handle error silently
        }
    }
} 