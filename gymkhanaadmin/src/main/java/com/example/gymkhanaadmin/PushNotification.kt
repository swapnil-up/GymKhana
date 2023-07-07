package com.example.gymkhanaadmin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class PushNotification : AppCompatActivity() {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextMessage: EditText
    private lateinit var notificationManager: NotificationManager
    private val channelId = "my_channel_id"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_push_notification)

        // Check if a FirebaseApp with the name "DEFAULT" already exists
        if (FirebaseApp.getApps(this).isEmpty()) {
            // Initialize Firebase app with the specified region
            FirebaseApp.initializeApp(
                this, FirebaseOptions.Builder()
                    .setDatabaseUrl("https://gymkhana-5560f-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .setApiKey("AIzaSyCxnhwb6BO4fyUF5Sv8rQUIWxBUZdVhCCM")
                    .setApplicationId("com.example.gymkhanaadmin")
                    .build()
            )
        }

        editTextTitle = findViewById(R.id.editTextTitle)
        editTextMessage = findViewById(R.id.editTextMessage)

        val buttonSend: Button = findViewById(R.id.buttonSend)
        buttonSend.setOnClickListener {
            val title = editTextTitle.text.toString()
            val message = editTextMessage.text.toString()

            // Create the notification channel
            createNotificationChannel()

            // Get a reference to the database with the specified region
            val database = FirebaseDatabase.getInstance("https://gymkhana-5560f-default-rtdb.asia-southeast1.firebasedatabase.app/")
            val tokensRef = database.getReference("FCM Tokens")

            tokensRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.d("fcmTokenLog", "FCM Token Count: ${dataSnapshot.childrenCount}")
                    // Iterate through the dataSnapshot to retrieve FCM tokens
                    for (tokenSnapshot in dataSnapshot.children) {
                        val fcmToken = tokenSnapshot.key // Use tokenSnapshot.key instead of getValue
                        Log.d("fcmTokenLog", "FCM Token: $fcmToken")

                        // Send a notification to the FCM token
                        if (fcmToken != null) {
                            sendNotification(title, message, fcmToken)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle the error case
                }
            })
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "My Channel"
            val channelDescription = "Notification Channel"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(title: String, message: String, fcmToken: String) {

        if (fcmToken.isNullOrEmpty()) {
            // Invalid token, show an error message or handle it appropriately
            return
        }

        Log.d("PushNotificationLog", "Notification Title: $title")
        Log.d("PushNotificationLog", "Notification Message: $message")

        val notification = RemoteMessage.Builder(fcmToken)
            .setMessageId(java.lang.String.valueOf(System.currentTimeMillis()))
            .addData("title", title)
            .addData("message", message)
            .build()

        Log.d("PushNotificationLog", "Notification Object: $notification")

        try {
            FirebaseMessaging.getInstance().send(notification)
            Log.d("PushNotificationLog2", "Notification Object: $notification")
            // Notification sent successfully
            showSuccessMessage()
        } catch (e: Exception) {
            // Failed to send notification
            showErrorMessage()
        }
    }

    private fun showSuccessMessage() {
        Toast.makeText(this, "Notification sent successfully", Toast.LENGTH_SHORT).show()
    }

    private fun showErrorMessage() {
        Toast.makeText(this, "Failed to send notification", Toast.LENGTH_SHORT).show()
    }
}
