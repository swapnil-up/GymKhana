package com.example.gymkhana

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
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
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        // Handle the new token (e.g., send it to your server)
        // You can also save the token locally for later use
        // Log the token to the console for testing purposes
        Log.d("FCM Token", token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Handle the data payload when the app is in the foreground
        if (remoteMessage.data.isNotEmpty()) {
            // Retrieve the data payload
            val data = remoteMessage.data

            // Filter out empty nodes from the data payload
            val filteredData = filterEmptyNodes(data)

            // Handle the filtered data payload as per your requirements
            handleDataPayload(filteredData)

        }
    }

    private fun filterEmptyNodes(data: Map<String, String>): Map<String, String> {
        val filteredData = mutableMapOf<String, String>()
        for ((key, value) in data) {
            if (value.isNotBlank()) {
                filteredData[key] = value
            }else {
                Log.d("FilterEmptyNodes", "Filtered out empty node - Key: $key, Value: $value")
            }
        }
        return filteredData
    }


    private fun handleDataPayload(data: Map<String, String>) {
        // Retrieve the values from the data payload
        val title = data["title"]
        val message = data["message"]
        Log.d("MyFirebaseMessagingService", "Title: $title")
        Log.d("MyFirebaseMessagingService", "Message: $message")

        // Save the notification to the Firebase Realtime Database
        saveNotificationToDatabase(title, message)
        // Show the data payload in the system tray/notification bar
        showDataPayloadNotification(title, message)
    }

    private fun showDataPayloadNotification(title: String?, message: String?) {
        if (title.isNullOrEmpty() && message.isNullOrEmpty()) {
            // Skip creating the notification if the title or message is empty
            return
        }

        val channelId = "heads_up_notification"
        val notificationId = 1

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(channelId, "Data Payload Notification", notificationManager)
        }

        val intent = Intent(this, NotificationActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("title", title)
        intent.putExtra("message", message)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.notification) // Set a valid small icon for the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        channelId: String,
        channelName: String,
        notificationManager: NotificationManager
    ) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance)
        notificationManager.createNotificationChannel(channel)
    }

    private fun saveNotificationToDatabase(title: String?, message: String?) {
        if (title.isNullOrEmpty() || message.isNullOrEmpty()) {
            Log.d("SaveNotification", "Skipping empty notification")
            return
        }
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
                                Log.d("ShowValue", "Value is: $value")
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
