package com.example.gymkhanaadmin.classes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gymkhanaadmin.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class UpdateClasses : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_classes)

        val fab: FloatingActionButton = findViewById(R.id.fab)

        fab.setOnClickListener{
            val intent = Intent(this, AddClasses::class.java)
        }

    }
}