package com.example.gymkhanaadmin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Handle the "Open" action here, such as displaying relevant information or navigating to a specific screen.
        // You can access the data passed from the notification intent using the intent extras.
        // For example, you can retrieve the notification title and message like this:
        val title = intent.getStringExtra("title")
        val message = intent.getStringExtra("message")

        // Display the notification title and message
        Toast.makeText(this, "Title: $title\nMessage: $message", Toast.LENGTH_SHORT).show()

        // Add your desired logic here based on the notification data
        // For example, you can start a new activity, update UI components, etc.

        // Finish the activity
        finish()
    }
}
