package com.example.gymkhana

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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

        /*// Create sample notification data
        val notificationList = listOf(
            Notification("Notification 1", "This is the first notification"),
            Notification("Notification 2", "This is the second notification"),
            Notification("Notification 3", "This is the third notification")
        )*/

        // Create and set the adapter for RecyclerView
        notificationAdapter = NotificationAdapter(notifications)
        notificationRecyclerView.adapter = notificationAdapter

        // Get a reference to the Firebase Realtime Database
        val database = FirebaseDatabase.getInstance("https://gymkhana-5560f-default-rtdb.asia-southeast1.firebasedatabase.app/")
        // Get a reference to the "notifications" node in the database
        val notificationsRef = database.reference.child("Notification")
        // Set up a ValueEventListener to listen for changes in the notifications node
        val valueEventListener = object : ValueEventListener {
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
                Log.e("NotificationActivity", "Failed to retrieve notifications: ${databaseError.message}")

                // Show a toast or display an error message to the user
                Toast.makeText(applicationContext, "Failed to retrieve notifications", Toast.LENGTH_SHORT).show()
            }
        }

        // Start listening for changes in the notifications node
        notificationsRef.addValueEventListener(valueEventListener)



    }
}
