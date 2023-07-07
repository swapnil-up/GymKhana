package com.example.gymkhanaadmin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Handle the "Open" action here, such as displaying relevant information or navigating to a specific screen.
        // You can access the data passed from the notification intent using the intent extras.
        // For example, you can retrieve the notification title and message like this:
        val title = intent.getStringExtra("title")
        val message = intent.getStringExtra("message")

        // Add your desired logic here based on the notification data

        // Finish the activity
        finish()
    }
}
