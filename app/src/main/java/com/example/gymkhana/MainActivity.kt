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

}

