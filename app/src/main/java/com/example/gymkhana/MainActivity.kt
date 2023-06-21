package com.example.gymkhana

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val imagebtn : ImageButton= findViewById(R.id.userIcon)
        val attendanceButton: Button = findViewById(R.id.attendance)

        imagebtn.setOnClickListener(){
            var i = Intent(this,UserDetails::class.java)
            startActivity(i)
        }


        attendanceButton.setOnClickListener {
            val attendanceFragment = AttendanceFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.attendance, attendanceFragment)
                .commit()
        }



    }

}

