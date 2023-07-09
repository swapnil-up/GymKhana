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

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()

        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("AuthToken", null)

        if (authToken == null || firebaseAuth.currentUser == null) {
            // User is not logged in, redirect to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Optional: Close the main activity to prevent returning to it
        } else {
            // User is logged in
            // Update the UI or perform any other necessary actions
        }

        val imagebtn: ImageButton = findViewById(R.id.userIcon)
        val attendanceButton: Button = findViewById(R.id.attendance)
        val notificationbtn: Button = findViewById(R.id.notificationbtn)
        val storeButton: Button = findViewById(R.id.Store)
        val foodButton: Button = findViewById(R.id.FoodSearch)
        val mealPlanButton: Button = findViewById(R.id.mealPlan)
        val imageButton: ImageButton = findViewById(R.id.logoutbutton)
        val scanButton: ImageButton = findViewById(R.id.scanButton)

        scanButton.setOnClickListener {
            openQRScanner()
        }

        imagebtn.setOnClickListener {
            val i = Intent(this, UserDetails::class.java)
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
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val result: IntentResult? =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents != null) {
            // QR code scanned successfully, handle the result
            val scannedData: String = result.contents
            Toast.makeText(this, "Scanned QR Code: $scannedData", Toast.LENGTH_LONG).show()
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
    }// Optional: Close the main activity to prevent
    }