package com.example.gymkhana

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class StoreActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var storeAdapter: StoreAdapter
    private val itemList: MutableList<StoreItem> = mutableListOf()
    private lateinit var databaseRef: DatabaseReference
    private lateinit var valueEventListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewItems)
        recyclerView.layoutManager = LinearLayoutManager(this)
        storeAdapter = StoreAdapter(itemList)
        recyclerView.adapter = storeAdapter

        // Get a reference to the Firebase Realtime Database
        val database = FirebaseDatabase.getInstance("https://gymkhana-5560f-default-rtdb.asia-southeast1.firebasedatabase.app")
        // Get a reference to the "items" node in the database
        databaseRef = database.reference.child("items")
        // Set up a ValueEventListener to listen for changes in the items node
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val itemList = mutableListOf<StoreItem>()

                // Iterate through the dataSnapshot to retrieve the items
                for (snapshot in dataSnapshot.children) {
                    val imageURL = snapshot.child("imageURL").getValue(String::class.java)
                    val name = snapshot.child("name").getValue(String::class.java)
                    val price = snapshot.child("price").getValue(Double::class.java)

                    if (imageURL != null && name != null && price != null) {
                        val item = StoreItem(imageURL, name, price)
                        itemList.add(item)
                    }
                }

                // Update the existing adapter's data list and notify it of the data change
                storeAdapter.updateItemList(itemList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Log the error message
                Log.e("StoreActivity", "Failed to retrieve items: ${databaseError.message}")

                // Show a toast or display an error message to the user
                Toast.makeText(applicationContext, "Failed to retrieve items", Toast.LENGTH_SHORT).show()
            }
        }

        // Attach the ValueEventListener to the database reference
        databaseRef.addValueEventListener(valueEventListener)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Remove the ValueEventListener when the activity is destroyed to avoid memory leaks
        databaseRef.removeEventListener(valueEventListener)
    }
}
