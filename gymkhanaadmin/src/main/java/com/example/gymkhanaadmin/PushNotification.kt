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
    private val FCM_CHANNEL_ID = "my_channel_id"
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
        val notification = JSONObject()
        try {

            notification.put("title", title)
            notification.put("body", message)

            json.put("notification", notification)
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
                showErrorMessage()
            }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "key=MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCcGmAIrefJ0fQB/arOH4JwvQ3wKhLjYaQhr+HxoJlusT6fD2405e6gaiJAAaPlD4Yd4ulSTU2mIZsg6+HSVXaDGFBWPDyVUkVi43/4MzBOKTynMQ5f774U/VI8xMC3WV10HV/+aBhdYtQVr3SomumuJ3oxslBvm4lE/7L9Yyxv5U8e9HXNKBPhPn8jwxbxBrhhi061CT+EgTI9XSznb6M1msybuJ9jgJEOaZBkmYlMA4DTxOKVfgi4cE3tnM9mDxfghISnMLc1Ov1x/SQMSRp8yXArWzRPs2Sg/VelRJOqkCAbUK0xXGknZYUU0WcLJyXDgjeJxrBO7Kt2WdrAKkYNAgMBAAECggEACOiIanZiL+ZHYoWzZafurbhcP0RnZ4p+7102K6/akbKTvEgJJgOpJoZoBfcR/1Rvdu0oG4ZF1CE+1RYhAPvXk4ujqggt/OeBBi+tceCZ9RvtVi06gMcRteju9IDV7qCu8J1siPwvx8SIDeTBZ43PoByooIVoibX6k3F0KXmrY4c/ p+jZCjxlh0oAxtIJmoeW4cI1NSe26gDxwLvMGSFnDfSn8HqQe+bsU19/3Gdkr1Wn 1blP7bSPHYI8vxdINil6nZjVIBcPlx/T0Ywqx7OMDgjFfHsOVx451uIcsSm4kfod sV/GmIACw7oSsQ1IEPN//IGavXZO+tDPTllQgjD/kQKBgQDXIltIGv5djGv7z4Z/ kpu4s+J90EaExGkB4jRu2H95GKbuk+F67VRK2LZvaQstlNnnuplJGiKIHu+ii3FUTBvJ6cHEhlQ+53FZa7thjTxoYDBYH4IQfn7kZadofVFq4GuXKdv5nUka8/8Vo4cMANkMIcTpMGGv+kQbOtyGFdvZuwKBgQC5wWx8EIZFjG9k4ZjU7yzAELMEaTcWoOQvJWONQk8oybz6bYV8LnqQhntTpEQVteg3q0ydp/Pzc+ZeFPULBQaw3igkjHNalY/xJBoJay1LVGVhEHczjHF99ynhyiVrV8kcAch8QKmrlVyv2D1pH/b39FE1OUnde0c4ZrT9W5+e1wKBgQDIvI8N/doAsgkEIkSufr78niSlHpTeR2Jv1oD0OODgvobssGHBUPfJCuNXm11Jv81/ctaapl84Qh15vsEVVhrL0WjzFiA/vbc/J83lHWMTRUV2xJeZCl8egFevoNc6cYMSvoU6KW/QTYFj0H0vTw83Sb8xkupjyJKKEec42eaVBwKBgQCaFCgBTM+jdtabbkmQLogHFJL5ULDiMzizJqdJ77urkJMRgrEbjY4avYIkoffbrlTdgFh/2WWQBg4K8gVES7n+EXhowJuagr6v/gsezuj2OB4Tgk3t00v8eX0jDcM2I83sTkpXTmeurKkCLzLZNSttBxopNMjhTzWHQiJUmaBwZwKBgH9dCUMuk1GLYljQnw3UMTf5+Byn4L/EPxjw+IL5LjjB5S/VavaFui2DtYVTAoGxGCaomhMJSji0SF6xZ5kfqlTlVVTdMZ/6mBcS1DtODUt/C6tXILdPHpZgMIAs5wJE0/CWeA3dokUkgWa9FVRF 4RWsziew3nhDDoI56GT2RyUI"
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
