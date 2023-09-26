package com.example.gymkhanaadmin


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import com.example.gymkhanaadmin.classes.AddItemActivity
import com.example.gymkhanaadmin.classes.UpdateClasses
import com.google.firebase.FirebaseApp
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
        val updateClasses :Button= findViewById(R.id.updateClasses)
        val QRgenerator:Button=findViewById(R.id.QRgenerator)



        /*imagebtn.setOnClickListener {
            var i = Intent(this,UserDetails::class.java)
            startActivity(i)
        }


        attendanceButton.setOnClickListener {
            val intent = Intent(this, AttendanceActivity::class.java)
            startActivity(intent)
        }
*/
        notificationbtn.setOnClickListener {
            val intent = Intent(this, PushNotification::class.java)
            startActivity(intent)
        }

        updateClasses.setOnClickListener{
            val intent = Intent(this, UpdateClasses::class.java)
            startActivity(intent)
        }

        QRgenerator.setOnClickListener {
            val intent = Intent(this, QRgeneratorActivity::class.java)
            startActivity(intent)
        }

        storeButton.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }
        /*

        foodButton.setOnClickListener {
            val intent = Intent(this, FoodSearchActivity::class.java)
            startActivity(intent)
        }

        mealPlanButton.setOnClickListener {
            val intent = Intent(this, MealPlanActivity::class.java)
            startActivity(intent)
        }
        */

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

