package com.example.gymkhana

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.bumptech.glide.Glide
import android.app.Activity
import android.app.Application
import android.net.Uri
import com.bumptech.glide.request.RequestOptions
import com.example.gymkhana.databinding.ActivityMainBinding
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storageReference: StorageReference
    private lateinit var database: FirebaseDatabase
    private lateinit var binding: ActivityMainBinding
    private lateinit var userId: String

    companion object {
        private const val USER_DETAILS_REQUEST_CODE = 1001 // Define a request code
    }
    class MyApplication : Application() {
        override fun onCreate() {
            super.onCreate()
            FirebaseApp.initializeApp(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        database =
            FirebaseDatabase.getInstance("https://gymkhana-5560f-default-rtdb.asia-southeast1.firebasedatabase.app/")
        storageReference = FirebaseStorage.getInstance().reference

        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("AuthToken", null)

        if (authToken == null || firebaseAuth.currentUser == null) {
            // User is not logged in, redirect to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Optional: Close the main activity to prevent returning to it
        }

        val imagebtn: ImageButton = binding.userIcon
        val attendanceButton: Button = binding.attendance
        val notificationbtn: Button = binding.notificationbtn
        val storeButton: Button = binding.Store
        val foodButton: Button = binding.FoodSearch
        val mealPlanButton: Button = binding.mealPlan
        val imageButton: ImageButton = binding.logoutbutton
        val scanButton: ImageButton = binding.scanButton
        val payButton: Button = binding.payment
        val joinClassBtn:Button=binding.JoinClassBtn

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        storageReference = FirebaseStorage.getInstance().reference

        // Load the user's profile photo
        loadUserProfilePhoto()

        if (userId != null) {
            val userReference: DatabaseReference = database.reference.child("Users").child(userId)
            userReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userDetails: HashMap<String, String>? =
                            snapshot.value as? HashMap<String, String>
                        if (userDetails != null) {
                            val firstName = userDetails["firstName"] ?: ""
                            val lastName = userDetails["lastName"] ?: ""
                            // Set the first name in the TextView
                            binding.userName.text = firstName
                            binding.lname.text = lastName
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the onCancelled event
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to retrieve user details",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

        scanButton.setOnClickListener {
            openQRScanner()
        }

        imagebtn.setOnClickListener {
            // Start UserDetails activity for result
            val i = Intent(this, UserDetails::class.java)
            startActivityForResult(i, USER_DETAILS_REQUEST_CODE)
        }

        payButton.setOnClickListener {
            val i = Intent(this, PaymentActivity::class.java)
            startActivity(i)
        }

        joinClassBtn.setOnClickListener {
            val i = Intent(this, JoinClassesActivity::class.java)
            startActivity(i)
        }

        imageButton.setOnClickListener {
            logout()
        }

        attendanceButton.setOnClickListener {
            val intent = Intent(this, AttendanceActivity::class.java)
            startActivity(intent)
        }

        notificationbtn.setOnClickListener {
            // Redirect to NotificationActivity without passing any extra data
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        storeButton.setOnClickListener {
            val intent = Intent(this, StoreActivity::class.java)
            startActivity(intent)
        }

        foodButton.setOnClickListener {
            val intent = Intent(this, FoodSearchActivity::class.java)
            startActivity(intent)
        }

        mealPlanButton.setOnClickListener {
            val intent = Intent(this, MealPlanActivity::class.java)
            startActivity(intent)
        }

        FirebaseApp.initializeApp(this)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM Token", token ?: "Token retrieval failed")
            } else {
                Log.e("FCM Token", "Token retrieval failed", task.exception)
            }
        }
    }

    private fun openQRScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setOrientationLocked(true)
        integrator.setBeepEnabled(false)
        integrator.setPrompt("Scan a QR Code")
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == USER_DETAILS_REQUEST_CODE) {
            if (resultCode == UserDetails.RESULT_CODE_PROFILE_UPDATED) {
                // User's profile has been updated, reload the user's profile photo
                loadUserProfilePhoto()
            }
        }

        val result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents != null) {
            // QR code scanned successfully, handle the result
            val scannedData: String = result.contents
            Toast.makeText(this, "Scanned QR Code: $scannedData", Toast.LENGTH_LONG).show()

            val userId = firebaseAuth.currentUser?.uid
            if (userId != null) {
                val attendanceRef: DatabaseReference = database.reference.child("Attendance")
                val newAttendanceRef = attendanceRef.child(userId).push()

                // Get the current date and time and format them
                val currentDateTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())

                newAttendanceRef.child("dateTime").setValue(currentDateTime)
                newAttendanceRef.child("message").setValue(scannedData)
                    .addOnSuccessListener {
                        // Successfully stored the scanned data in the "Attendance" node

                        // Create a new notification entry under the current user's node
                        val userNotificationsRef: DatabaseReference = database.reference
                            .child("Users")
                            .child(userId)
                            .child("Notifications")

                        val newNotificationRef = userNotificationsRef.push()
                        newNotificationRef.child("title").setValue(currentDateTime)
                        newNotificationRef.child("message").setValue(scannedData)

                        // Create a new notification entry in the "Notifications" node for all users
                        val notificationsRef: DatabaseReference = database.reference.child("Notifications")
                        val newAdminNotificationRef = notificationsRef.push()
                        newAdminNotificationRef.child("title").setValue(currentDateTime)
                        newAdminNotificationRef.child("message").setValue(scannedData)
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Failed to store scanned data", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            // QR code scanning canceled or failed
            Toast.makeText(this, "Scan canceled or failed", Toast.LENGTH_LONG).show()
        }
    }

    private fun logout() {
        firebaseAuth.signOut()
        Toast.makeText(this, "Log out Successful", Toast.LENGTH_LONG).show()

        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("AuthToken")
        editor.apply()

        // Redirect the user to the login screen
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun loadUserProfilePhoto() {
        // Load the user's profile photo URL from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val userPhotoUrl = sharedPreferences.getString("userPhotoUrl", null)

        // Check if the user has a profile photo URL
        if (userPhotoUrl != null) {
            // Load the user's photo using Glide into imagebtn (user icon)
            Glide.with(this@MainActivity)
                .load(userPhotoUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.userIcon)
        }
    }
}
