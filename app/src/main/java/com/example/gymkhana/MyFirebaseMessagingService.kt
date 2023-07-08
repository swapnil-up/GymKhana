package com.example.gymkhana

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.Constants.TAG
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        // Handle the new token (e.g., send it to your server)
        // You can also save the token locally for later use
        // Log the token to the console for testing purposes
        Log.d("FCM Token", token)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        // Handle the incoming notification here
        // You can access the notification title, body, and other data from the `remoteMessage` parameter
        Log.d(
            "MyFirebaseMessaging",
            "Received notification: ${remoteMessage.notification?.title} - ${remoteMessage.notification?.body}"
        )
        // Handle the received notification
        val title = remoteMessage.notification?.title
        val message = remoteMessage.notification?.body
        val text = remoteMessage.notification?.body

        // Save the notification to the Firebase Realtime Database
        saveNotificationToDatabase(title, message)

        val CHANNEL_ID = "HEADS_UP_NOTIFICATION"

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Heads Up Notification",
            NotificationManager.IMPORTANCE_HIGH
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(1, notification)

        super.onMessageReceived(remoteMessage)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channelId = "HEADS_UP_NOTIFICATION"
        val channelName = "Heads Up Notification"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(channelId, channelName, importance)
        channel.enableVibration(true)
        channel.setShowBadge(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun saveNotificationToDatabase(title: String?, message: String?) {
        Log.d("myNotificationReceived", "Title: $title")
        Log.d("myNotificationReceived", "Message: $message")

        val databaseUrl = "https://gymkhana-5560f-default-rtdb.asia-southeast1.firebasedatabase.app/"

        val database = FirebaseDatabase.getInstance(databaseUrl).reference.child("Notification")

        val notificationKey = database.push().key

        val notification = Notification(title ?: "", message ?: "")

        if (notificationKey != null) {
            database.child(notificationKey).setValue(notification)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("myNotificationReceived", "Notification saved to Firebase Realtime Database")

                        val myRef = database.child(notificationKey)
                        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val value = dataSnapshot.getValue(Notification::class.java)
                                Log.d(TAG, "Value is: $value")
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Log.e(
                                    "myNotificationReceivedFailedRead",
                                    "Failed to read value.",
                                    databaseError.toException()
                                )
                            }
                        })
                    } else {
                        Log.e(
                            "myNotificationReceivedFailedSave",
                            "Failed to save notification to Firebase Realtime Database",
                            task.exception
                        )
                    }
                }
        }
    }
}
