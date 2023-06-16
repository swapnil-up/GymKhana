package com.example.gymkhanaadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imagebtn : ImageButton = findViewById(R.id.adminIcon)

        imagebtn.setOnClickListener(){
            var i = Intent(this,AdminDetails::class.java)
            startActivity(i)
        }

        }
}