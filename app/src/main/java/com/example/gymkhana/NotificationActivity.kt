package com.example.gymkhana

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class NotificationActivity : AppCompatActivity() {

    private lateinit var notificationRecyclerView: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter
    private val notifications: MutableList<Notification> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        // Initialize RecyclerView
        notificationRecyclerView = findViewById(R.id.notificationRecyclerView)
        notificationRecyclerView.layoutManager = LinearLayoutManager(this)

        // Create and set the adapter for RecyclerView
        notificationAdapter = NotificationAdapter(notifications)
        notificationRecyclerView.adapter = notificationAdapter

        // Get the current user's ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Get a reference to the Firebase Realtime Database
        val databaseUrl = "https://gymkhana-5560f-default-rtdb.asia-southeast1.firebasedatabase.app/"
        val database = FirebaseDatabase.getInstance(databaseUrl).reference

        if (userId != null) {
            // Get a reference to the current user's Notifications node
            val userNotificationsRef = database
                .child("Users")
                .child(userId)
                .child("Notifications")

            // Set up a ValueEventListener to listen for changes in the user's notifications
            val userValueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val notificationList = mutableListOf<Notification>()

                    // Iterate through the dataSnapshot to retrieve the notifications
                    for (snapshot in dataSnapshot.children) {
                        val notification = snapshot.getValue(Notification::class.java)
                        if (notification != null) {
                            notificationList.add(notification)
                        }
                    }

                    // Update the existing adapter's data list and notify it of the data change
                    notifications.clear()
                    notifications.addAll(notificationList)
                    notificationAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Log the error message
                    Log.e("NotificationActivity", "Failed to retrieve user-specific notifications: ${databaseError.message}")

                    // Show a toast or display an error message to the user
                    Toast.makeText(applicationContext, "Failed to retrieve notifications", Toast.LENGTH_SHORT).show()
                }
            }

            // Start listening for changes in the user's notifications
            userNotificationsRef.addValueEventListener(userValueEventListener)
        }

        // Get a reference to the general Notifications node for all users
        val adminNotificationsRef = database.child("Notifications")

        // Set up a ValueEventListener to listen for changes in the general notifications
        val adminValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val notificationList = mutableListOf<Notification>()

                // Iterate through the dataSnapshot to retrieve the notifications
                for (snapshot in dataSnapshot.children) {
                    val notification = snapshot.getValue(Notification::class.java)
                    if (notification != null) {
                        notificationList.add(notification)
                    }
                }

                // Update the existing adapter's data list and notify it of the data change
                notifications.addAll(notificationList)
                notificationAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Log the error message
                Log.e("NotificationActivity", "Failed to retrieve general notifications: ${databaseError.message}")

                // Show a toast or display an error message to the user
                Toast.makeText(applicationContext, "Failed to retrieve notifications", Toast.LENGTH_SHORT).show()
            }
        }

        // Start listening for changes in the general notifications
        adminNotificationsRef.addValueEventListener(adminValueEventListener)
    }


}
