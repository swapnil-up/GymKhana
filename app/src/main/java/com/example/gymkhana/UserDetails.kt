package com.example.gymkhana

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gymkhana.databinding.ActivityUserDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UserDetails : AppCompatActivity() {
    private lateinit var binding: ActivityUserDetailsBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var userId: String
    private lateinit var storageReference: StorageReference
    private lateinit var selectedImageUri: Uri

    companion object {
        private const val PICK_IMAGE_REQUEST = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance("https://gymkhana-5560f-default-rtdb.asia-southeast1.firebasedatabase.app/")
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        storageReference = FirebaseStorage.getInstance().reference



        // Retrieve user details from Firebase Realtime Database
        val userReference: DatabaseReference = database.reference.child("Users").child(userId)
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userDetails: HashMap<String, String>? = snapshot.value as? HashMap<String, String>
                    if (userDetails != null) {
                        val name = userDetails["name"]
                        val email = userDetails["email"]
                        val phone = userDetails["phone"]

                        // Set user details in the appropriate views
                        binding.nameEditText.setText(name)
                        binding.emailEditText.setText(email)
                        binding.phoneEditText.setText(phone)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@UserDetails, "Failed to retrieve user details", Toast.LENGTH_SHORT).show()
            }
        })

        binding.uploadPhotoButton.setOnClickListener {
            openImageChooser()
        }

        binding.saveButton.setOnClickListener {
            saveUserDetails()
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        }
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data!!
            binding.userPhotoImageView.setImageURI(selectedImageUri)
        }
    }

    private fun saveUserDetails() {
        val name = binding.nameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val phone = binding.phoneEditText.text.toString().trim()

        if (name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty()) {
            val userReference: DatabaseReference = database.reference.child("Users").child(userId)
            val userDetails = HashMap<String, String>()
            userDetails["name"] = name
            userDetails["email"] = email
            userDetails["phone"] = phone

            userReference.setValue(userDetails).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User details saved successfully", Toast.LENGTH_SHORT).show()
                    uploadUserPhoto()
                } else {
                    Toast.makeText(this, "Failed to save user details", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadUserPhoto() {
        val imageRef = storageReference.child("user_photos").child(userId)
        imageRef.putFile(selectedImageUri)
            .addOnSuccessListener { taskSnapshot ->
                Toast.makeText(this, "User photo uploaded successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to upload user photo", Toast.LENGTH_SHORT).show()
            }
    }
}
