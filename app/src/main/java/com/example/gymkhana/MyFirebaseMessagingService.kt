package com.example.gymkhana

import android.util.Log
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
    }
    private fun saveNotificationToDatabase(title: String?, message: String?) {
        // Get a reference to the Firebase Realtime Database
        val database = FirebaseDatabase.getInstance().reference

        // Generate a unique key for the notification entry
        val notificationKey = database.child("notifications").push().key

        // Create a notification object
        val notification = Notification(title ?: "", message ?: "")

        // Save the notification to the Firebase Realtime Database
        if (notificationKey != null) {
            database.child("notifications").child(notificationKey).setValue(notification)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Notification saved successfully
                        Log.d(TAG, "Notification saved to Firebase Realtime Database")

                        // Read the saved notification from the database
                        val myRef = database.child("notifications").child(notificationKey)
                        myRef.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                // This method is called once with the initial value and again
                                // whenever data at this location is updated.
                                val value = dataSnapshot.getValue(Notification::class.java)
                                Log.d(TAG, "Value is: $value")
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException())
                            }
                        })
                    } else {
                        // Failed to save notification
                        Log.e(TAG, "Failed to save notification to Firebase Realtime Database", task.exception)
                    }
                }
        }
    }
}

