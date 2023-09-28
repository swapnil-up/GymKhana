package com.example.gymkhana

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymkhana.classes.ClassesAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class JoinClassesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var classesAdapter: ClassesAdapter
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_classes)

        // Initialize RecyclerView and its adapter
        recyclerView = findViewById(R.id.classesList)
        classesAdapter = ClassesAdapter(emptyList(), onJoinClick, onLeaveClick)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = classesAdapter

        // Initialize Firebase Realtime Database reference
        databaseRef = FirebaseDatabase.getInstance("https://gymkhana-5560f-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("classes")

        // Load classes data into the adapter
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val classesList = mutableListOf<GymClass>()

                for (classSnapshot in snapshot.children) {
                    val gymClass = classSnapshot.getValue(GymClass::class.java)
                    gymClass?.let {
                        classesList.add(it)
                    }
                }

                // Update the RecyclerView adapter with the loaded data
                classesAdapter.onDataChanged(classesList)

                // Toggle visibility of RecyclerView and "No Data" view
                if (classesList.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    findViewById<View>(R.id.clsNoData).visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    findViewById<View>(R.id.clsNoData).visibility = View.GONE
                }
            }

            val user = FirebaseAuth.getInstance().currentUser
            val userId = user?.uid

            // Assuming you have a reference to the database
            val userClassesRef = FirebaseDatabase.getInstance()
                .getReference("user_classes/$userId/joined_classes")

            override fun onCancelled(error: DatabaseError) {
                Log.e("UserClasses", "Error loading classes data: ${error.message}")
            }
        })
    }

    private val onJoinClick: (Int) -> Unit = { position ->
        // Handle the join click for the class at the given position
        val classId = classesAdapter.getItemId(position).toString() // Get the class ID using your adapter's logic

        // Get the current user's ID
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        // Assuming you have a reference to the database
        val userClassesRef = FirebaseDatabase.getInstance()
            .getReference("Users/$userId/JoinedClasses")


        // Set the value to true to indicate that the user has joined this class
        userClassesRef.child(classId).setValue(true){ databaseError, _ ->
            if (databaseError == null) {
                // The join operation was successful
                Log.d("JoinClassesActivity2", "Join operation successful")
            } else {
                // There was an error during the join operation
                Log.e("JoinClassesActivity2", "Error during join operation: ${databaseError.message}")
            }
        }
    }

    private val onLeaveClick: (Int) -> Unit = { position ->
        // Handle the leave click for the class at the given position
        val classId = classesAdapter.getItemId(position).toString() // Get the class ID using your adapter's logic

        // Get the current user's ID
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        // Assuming you have a reference to the database
        val userClassesRef = FirebaseDatabase.getInstance()
            .getReference("Users/$userId/JoinedClasses")

        // Remove the class from the user's joined classes
        userClassesRef.child(classId).removeValue()
    }


}
