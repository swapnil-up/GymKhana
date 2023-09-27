package com.example.gymkhanaadmin.classes

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.gymkhanaadmin.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.UUID

class AddClasses : AppCompatActivity() {

    private lateinit var classNameEditText: EditText
    private lateinit var classImageView: ImageView
    private lateinit var addClassButton: Button
    private lateinit var addClassDescription: EditText


    private lateinit var storageRef: StorageReference
    private lateinit var databaseRef: DatabaseReference

    private var imageBitmap: Bitmap? = null

    private val getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { imageUri ->
                try {
                    val imageStream = contentResolver.openInputStream(imageUri)
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                    classImageView.setImageBitmap(imageBitmap)
                } catch (e: Exception) {
                    Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_classes)

        classNameEditText = findViewById(R.id.addClassName)
        classImageView = findViewById(R.id.addClassImg)
        addClassButton = findViewById(R.id.addClassBtn)
        addClassDescription = findViewById(R.id.addClassDescription)


            // Initialize Firebase Storage
        val storage = FirebaseStorage.getInstance()
        storageRef = storage.reference.child("class_images")

        // Initialize Firebase Realtime Database with your database URL
        val database = FirebaseDatabase.getInstance("https://gymkhana-5560f-default-rtdb.asia-southeast1.firebasedatabase.app")

        // Get a reference to the "classes" node in the database
        databaseRef = database.reference.child("classes")

        classImageView.setOnClickListener {
            takePicture()
        }

        addClassButton.setOnClickListener {
            addClass()
        }
    }

//    private fun takePicture() {
//        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        getImage.launch(takePictureIntent)
//
//
//    }

    private fun takePicture() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")

        AlertDialog.Builder(this)
            .setTitle("Choose an option")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> dispatchTakePictureIntent()
                    1 -> dispatchPickImageIntent()
                    2 -> dialog.dismiss()
                }
            }
            .show()
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        getImage.launch(takePictureIntent)
    }

    private fun dispatchPickImageIntent() {
        val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getImage.launch(pickImageIntent)
    }


    private fun addClass() {
        val className = classNameEditText.text.toString().trim()
        val classDesc = addClassDescription.text.toString().trim()

        if (className.isEmpty() || imageBitmap == null||classDesc.isEmpty()) {
            Toast.makeText(this, "Please fill in the class name, description and select an image", Toast.LENGTH_SHORT).show()
            return
        }

        // Generate a random UUID for the image filename
        val imageFileName = "${UUID.randomUUID()}.jpg"
        val imageRef = storageRef.child(imageFileName)

        // Convert the image to bytes
        val baos = ByteArrayOutputStream()
        imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageData = baos.toByteArray()

        // Upload the image to Firebase Storage
        imageRef.putBytes(imageData)
            .addOnSuccessListener {
                // Get the download URL for the uploaded image
                imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                    // Create a Class object
                    val classData = ClassData(className,classDesc, imageUrl.toString())

                    // Upload class data to Firebase Realtime Database
                    val classKey = databaseRef.push().key
                    if (classKey != null) {
                        databaseRef.child(classKey).setValue(classData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Class added successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to add class to the database", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }
}
