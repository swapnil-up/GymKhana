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
    private lateinit var joinedClassesRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_classes)

        // Initialize RecyclerView and its adapter
        recyclerView = findViewById(R.id.classesList)
        classesAdapter = ClassesAdapter(emptyList(), onJoinClick, onLeaveClick)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = classesAdapter

        // Initialize Firebase Realtime Database reference for classes and joined classes
        val database = FirebaseDatabase.getInstance("https://gymkhana-5560f-default-rtdb.asia-southeast1.firebasedatabase.app")
        databaseRef = database.getReference("classes")
        joinedClassesRef = database.getReference("JoinedClasses")

        // Get the current user's ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            // Load classes data into the adapter
            databaseRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val classesList = mutableListOf<GymClass>()

                    for (classSnapshot in snapshot.children) {
                        val gymClass = classSnapshot.getValue(GymClass::class.java)
                        gymClass?.let {
                            it.classId = classSnapshot.key.toString() // Set the classId property
                            classesList.add(it)                        }
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

                override fun onCancelled(error: DatabaseError) {
                    Log.e("UserClasses", "Error loading classes data: ${error.message}")
                }
            })

            // Check if the user is already joined any classes
            val userJoinedClassesQuery = joinedClassesRef.child(userId)

            userJoinedClassesQuery.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userJoinedClasses = snapshot.children.map { it.key ?: "" }

                    // Update the adapter to reflect the user's joined classes
                    classesAdapter.updateUserJoinedClasses(userJoinedClasses)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("JoinClassesActivity", "Error checking user's joined classes: ${error.message}")
                }
            })
        }
    }

    private val onJoinClick: (String) -> Unit = { classId ->
        // Get the current user's ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            // Add the user to the class in Firebase
            joinedClassesRef.child(userId).child(classId).setValue(true)
                .addOnSuccessListener {
                    // The join operation was successful
                    Log.d("JoinClassesActivity", "Join operation successful")
                }
                .addOnFailureListener { exception ->
                    // There was an error during the join operation
                    Log.e("JoinClassesActivity", "Error during join operation: ${exception.message}")
                }
        }
    }


    private val onLeaveClick: (String) -> Unit = { classId ->
        // Get the current user's ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            // Remove the user from the class
            joinedClassesRef.child(userId).child(classId).removeValue()
                .addOnSuccessListener {
                    // The leave operation was successful
                    Log.d("JoinClassesActivity", "Leave operation successful")
                }
                .addOnFailureListener { exception ->
                    // There was an error during the leave operation
                    Log.e("JoinClassesActivity", "Error during leave operation: ${exception.message}")
                }
        }
    }


}
