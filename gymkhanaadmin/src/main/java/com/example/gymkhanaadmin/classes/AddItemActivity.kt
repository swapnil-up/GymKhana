package com.example.gymkhanaadmin.classes

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.gymkhanaadmin.R
import com.example.gymkhanaadmin.StoreItem
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date
import java.util.Locale

class AddItemActivity : AppCompatActivity() {
    private lateinit var itemNameEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var capturePhotoButton: Button
    private lateinit var storageRef: StorageReference
    private lateinit var imageUri: Uri
    private lateinit var imageUrlEditText:EditText

    private lateinit var databaseRef: DatabaseReference

    private var itemName: String = ""
    private var price: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        itemNameEditText = findViewById(R.id.itemNameEditText)
        priceEditText = findViewById(R.id.priceEditText)
        capturePhotoButton = findViewById(R.id.capturePhotoButton)
        imageUrlEditText=findViewById(R.id.imageURLEditText)

        // Get a reference to the Firebase Storage
        val storage = FirebaseStorage.getInstance("gs://gymkhana-5560f.appspot.com/")
        // Create a reference to the root folder of the storage
        storageRef = storage.reference

        // Launch the camera app to capture the photo
        capturePhotoButton.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
        }

        // Get a reference to the Firebase Realtime Database
        val database = FirebaseDatabase.getInstance("https://gymkhana-5560f-default-rtdb.asia-southeast1.firebasedatabase.app")
        // Get a reference to the "items" node in the database
        databaseRef = database.reference.child("items")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            // Get the captured image from the intent extras
            val capturedImage = data?.extras?.get("data") as? Bitmap

            // Process the captured image or store it in a file
            if (capturedImage != null) {
                val directoryName = Environment.getExternalStorageDirectory().toString()
                val directory = File(Environment.getExternalStoragePublicDirectory(directoryName), "YourDirectoryName")
                if (!directory.exists()) {
                    directory.mkdirs()
                }
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val imageFileName = "JPEG_$timeStamp.jpg"
                val imageFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), imageFileName)
                try {
                    val fileOutputStream = FileOutputStream(imageFile)
                    capturedImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                    fileOutputStream.flush()
                    fileOutputStream.close()

                    val itemName = itemNameEditText.text.toString()
                    val price = priceEditText.text.toString().toDoubleOrNull()

                    if (itemName.isNotEmpty() && price != null) {
                        // Proceed with further actions
                        uploadImageToStorage(Uri.fromFile(imageFile), itemName, price)
                    } else {
                        Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed to save captured image", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImageToStorage(imageUri: Uri, itemName: String, price: Double) {
        val storageRef = storageRef.child("item_images/${imageUri.lastPathSegment}")
        val uploadTask = storageRef.putFile(imageUri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Image uploaded successfully, get the download URL
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                val item = StoreItem(uri.toString(), itemName, price)
                addItemToDatabase(item)
                // Set the download URL in the EditText
                imageUrlEditText.setText(uri.toString())
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Failed to retrieve download URL: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addItemToDatabase(item: StoreItem) {
        databaseRef.push().setValue(item)
            .addOnSuccessListener {
                Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
    }
}
