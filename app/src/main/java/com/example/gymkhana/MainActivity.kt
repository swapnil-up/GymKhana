package com.example.gymkhana

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import android.content.pm.PackageManager
import android.net.Uri
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val imagebtn : ImageButton= findViewById(R.id.userIcon)
        val attendanceButton: Button = findViewById(R.id.attendance)
        val notificationbtn: Button=findViewById(R.id.notificationbtn)
        val storeButton:Button=findViewById(R.id.Store)
        val foodButton :Button= findViewById(R.id.FoodSearch)
        val mealPlanButton :Button= findViewById(R.id.mealPlan)
        val imageButton : ImageButton= findViewById(R.id.logoutbutton)
        val scanButton : ImageButton= findViewById(R.id.scanButton)

        scanButton.setOnClickListener {
            openQRScanner()
        }


        imagebtn.setOnClickListener {
            var i = Intent(this,UserDetails::class.java)
            startActivity(i)
        }

        imageButton.setOnClickListener {
            Firebase.auth.signOut()
            var i = Intent(this,LoginActivity::class.java)
            startActivity(i)

            Toast.makeText(this,"Log out Successful ",Toast.LENGTH_LONG).show()
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
        integrator.setOrientationLocked(false)
        integrator.setBeepEnabled(false)
        integrator.setPrompt("Scan a QR Code")
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents != null) {
            // QR code scanned successfully, handle the result
            val scannedData: String = result.contents
            Toast.makeText(this, "Scanned QR Code: $scannedData", Toast.LENGTH_LONG).show()
        } else {
            // QR code scanning canceled or failed
            Toast.makeText(this, "Scan canceled or failed", Toast.LENGTH_LONG).show()
        }
    }
}

