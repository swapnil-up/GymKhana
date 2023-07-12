package com.example.gymkhanaadmin.classes

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.example.gymkhanaadmin.R

class AddClasses : AppCompatActivity() {
//    val PICK_IMAGE_REQUEST = 1

    lateinit var addClassImg: ImageView
    lateinit var addClassName: EditText
    lateinit var addClassDescription: EditText
    lateinit var addClassBtn: Button





    private val contract = registerForActivityResult(ActivityResultContracts.GetContent()){
        addClassImg.setImageURI(it)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_classes)


        addClassImg = findViewById(R.id.addClassImg)
        addClassName = findViewById(R.id.addClassName)
        addClassDescription = findViewById(R.id.addClassDescription)
        addClassBtn = findViewById(R.id.addClassBtn)




        addClassImg.setOnClickListener {
            contract.launch("image/*")
        }

    }



//    private fun openGallery() {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        startActivityForResult(intent, PICK_IMAGE_REQUEST)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
//            val selectedImageUri: Uri? = data.data
//            val bitmap: Bitmap? = getBitmapFromUri(selectedImageUri)
//            addClassImg.setImageBitmap(bitmap)
//            // Handle the selected image URI here
//            // You can pass it to an image upload function or perform any other operations
//        }
//
//    }
//    private fun getBitmapFromUri(uri: Uri?): Bitmap? {
//        return try {
//            val inputStream = contentResolver.openInputStream(uri!!)
//            BitmapFactory.decodeStream(inputStream)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }
}