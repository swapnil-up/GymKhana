package com.example.gymkhanaadmin

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.util.Date
import java.util.Locale

class CapturePhotoActivity : AppCompatActivity() {
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_photo)

        // Create a file to store the captured image
        val imageFile = createImageFile()

        // Create the content URI for the image file
        imageUri = FileProvider.getUriForFile(
            this,
            "com.example.gymkhanaadmin.fileprovider",
            imageFile
        )

        // Launch the camera app to capture the photo
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
        return imageFile
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Pass the captured image URI back to the calling activity
            val resultIntent = Intent().apply {
                putExtra(EXTRA_IMAGE_URI, imageUri)
            }
            setResult(RESULT_OK, resultIntent)
        } else {
            // If capturing the photo was not successful, set the result to cancelled
            setResult(RESULT_CANCELED)
        }

        // Close the activity
        finish()
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
}
