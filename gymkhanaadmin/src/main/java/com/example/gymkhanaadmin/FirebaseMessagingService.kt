package com.example.gymkhanaadmin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
        // Handle the incoming notification here
        // You can access the notification title, body, and other data from the `remoteMessage` parameter
        Log.d(
            "MyFirebaseMessaging",
            "Received notification: ${remoteMessage.notification?.title} - ${remoteMessage.notification?.body}"
        )

        // Handle the received notification
        val title = remoteMessage.notification?.title
        val message = remoteMessage.notification?.body

        // Save the notification to the Firebase Realtime Database
        saveNotificationToDatabase(title, message)

        // Create an intent to open the NotificationActivity
        val intent = Intent(this, NotificationActivity::class.java)
        intent.putExtra("title", title)
        intent.putExtra("message", message)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Create a notification channel (required for Android Oreo and above)
        createNotificationChannel()

        // Build the notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.notification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Display the notification
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun saveNotificationToDatabase(title: String?, message: String?) {
        // Replace the placeholder with your Firebase Realtime Database URL
        val databaseUrl = "https://your-firebase-database-url.firebaseio.com"

        // Get a reference to the Firebase Realtime Database
        val database = FirebaseDatabase.getInstance(databaseUrl).getReference("notifications")

        // Generate a unique key for the notification entry
        val notificationKey = database.push().key

        // Create a notification object
        val notification = Notification(title ?: "", message ?: "")

        // Save the notification to the Firebase Realtime Database
        if (notificationKey != null) {
            database.child(notificationKey).setValue(notification)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Notification saved successfully
                        Log.d("MyFirebaseMessaging", "Notification saved to Firebase Realtime Database")
                    } else {
                        // Failed to save notification
                        Log.e("MyFirebaseMessaging", "Failed to save notification to Firebase Realtime Database", task.exception)
                    }
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channelId = "YourChannelId"
        val channelName = "YourChannelName"
        val channelDescription= "YourChannelDescription"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(channelId, channelName, importance)
        channel.description = channelDescription

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "YourChannelId"
        private const val NOTIFICATION_ID = 1
    }
}
