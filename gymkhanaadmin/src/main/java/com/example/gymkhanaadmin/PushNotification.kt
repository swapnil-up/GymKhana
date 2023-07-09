package com.example.gymkhanaadmin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import com.android.volley.Request
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.json.JSONException
import org.json.JSONObject

class PushNotification : AppCompatActivity() {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextMessage: EditText
    private lateinit var notificationManager: NotificationManager
    private val FCM_CHANNEL_ID = "heads_up_notification"
    companion object {
        const val FCM_CHANNEL_ID = "FCM_CHANNEL_ID"
    }

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val fcmChannel = NotificationChannel(
                FCM_CHANNEL_ID,
                "FCM_Channel",
                NotificationManager.IMPORTANCE_HIGH
            )

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(fcmChannel)
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
            val channelName = "FCM Channel"
            val channelDescription = "Firebase Cloud Messaging Channel"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(FCM_CHANNEL_ID, channelName, importance).apply {
                description = channelDescription
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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

        val json = JSONObject()
        val data = JSONObject()
        try {
            data.put("title", title) // Add title to the data payload
            data.put("message", message) // Add message to the data payload
            json.put("data", data) // Include the data payload
            json.put("to", fcmToken)
            json.put("channel_id", FCM_CHANNEL_ID)
        } catch (e: JSONException) {
            e.printStackTrace()
            return
        }

        val requestBody = json.toString()

        val request = object : StringRequest(
            Request.Method.POST,
            "https://fcm.googleapis.com/fcm/send",
            Response.Listener<String> { response ->
                // Notification sent successfully
                showSuccessMessage()
            },
            Response.ErrorListener { error ->
                // Failed to send notification
                Log.e("PushNotificationLogFail", "Error sending notification: ${error.message}")
                showErrorMessage()
            }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "key=AAAAR2SHxnQ:APA91bES_MqK4Iau19kB9d_ZKvHxUFFnieUVu6ky9jtdTLrNshY_ZVUpcfLHsH-dkRZ9OcluVF_fd1YLMrnARUYZ2UvwcCTsEhM3S1ngGuwMTPD52NL_CPmfdHBLMXigAFHbOSgJwsb7"
                return headers
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun showSuccessMessage() {
        Toast.makeText(this, "Notification sent successfully", Toast.LENGTH_SHORT).show()
    }

    private fun showErrorMessage() {
        Toast.makeText(this, "Failed to send notification", Toast.LENGTH_SHORT).show()
    }
}
