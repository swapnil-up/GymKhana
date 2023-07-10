package com.example.gymkhanaadmin.classes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.gymkhanaadmin.R
import com.example.gymkhanaadmin.StoreItem
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddItemActivity : AppCompatActivity() {
    private lateinit var imageURLEditText: EditText
    private lateinit var itemNameEditText: EditText
    private lateinit var priceEditText: EditText
    // ... other UI elements

    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        imageURLEditText = findViewById(R.id.imageURLEditText)
        itemNameEditText = findViewById(R.id.itemNameEditText)
        priceEditText = findViewById(R.id.priceEditText)
        // ... initialize other UI elements

        val addItemButton = findViewById<Button>(R.id.addItemButton)
        addItemButton.setOnClickListener {
            val imageURL = imageURLEditText.text.toString()
            val itemName = itemNameEditText.text.toString()
            val price = priceEditText.text.toString().toDoubleOrNull()

            if (imageURL.isNotEmpty() && itemName.isNotEmpty() && price != null) {
                val item = StoreItem(imageURL, itemName, price)
                addItemToDatabase(item)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Get a reference to the Firebase Realtime Database
        val database = FirebaseDatabase.getInstance("https://gymkhana-5560f-default-rtdb.asia-southeast1.firebasedatabase.app")
        // Get a reference to the "items" node in the database
        databaseRef = database.reference.child("items")
    }

    private fun addItemToDatabase(item: StoreItem) {
        val newItemRef = databaseRef.push() // Generate a new unique key for the item
        newItemRef.setValue(item)
            .addOnSuccessListener {
                Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show()
            }
    }
}
