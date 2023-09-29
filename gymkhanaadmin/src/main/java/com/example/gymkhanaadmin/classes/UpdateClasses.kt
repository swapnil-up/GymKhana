package com.example.gymkhanaadmin.classes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymkhanaadmin.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UpdateClasses : AppCompatActivity() {

    private lateinit var fab: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var classesAdapter: ClassesAdapter
    private lateinit var databaseRef: DatabaseReference
    private lateinit var joinedClassesRecyclerView: RecyclerView
    private lateinit var joinedClassesAdapter: JoinedClassesAdapter
    private lateinit var joinedClassesRef: DatabaseReference
    private val joinedClassesList = mutableListOf<JoinedClass>() // Declare the list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_classes)

        // Initialize RecyclerView and its adapter
        recyclerView = findViewById(R.id.classesList)
        classesAdapter = ClassesAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = classesAdapter

        // Initialize RecyclerView and its adapter for joined classes
        joinedClassesRecyclerView = findViewById(R.id.joinedClassesList)
        joinedClassesAdapter = JoinedClassesAdapter(joinedClassesList)
        joinedClassesRecyclerView.layoutManager = LinearLayoutManager(this)
        joinedClassesRecyclerView.adapter = joinedClassesAdapter


        fab = findViewById(R.id.fab)

        fab.setOnClickListener {
            Log.d("UpdateClasses", "FloatingActionButton clicked")
            val intent = Intent(this, AddClasses::class.java)
            startActivity(intent)
        }

        // Initialize Firebase Realtime Database reference
        databaseRef =
            FirebaseDatabase.getInstance("https://gymkhana-5560f-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("classes")
        joinedClassesRef =
            FirebaseDatabase.getInstance("https://gymkhana-5560f-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("JoinedClasses")


        // Load classes data into the adapter
        loadClassesData()

        // Load joined classes data into the adapter
        loadJoinedClassesData()
    }

    private fun loadClassesData() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val classesMap = mutableMapOf<String, GymClass>()

                for (classSnapshot in snapshot.children) {
                    val key = classSnapshot.key ?: ""
                    val gymClass = classSnapshot.getValue(GymClass::class.java)
                    gymClass?.let {
                        classesMap[key] = it
                    }
                }

                // Extract the values from the map to a list
                val classesList = classesMap.values.toList()

                // Update the RecyclerView adapter with the loaded data
                classesAdapter.updateData(classesList)

                // Toggle visibility of RecyclerView and "No Data" view
                if (classesList.isEmpty()) {
                    recyclerView.visibility = android.view.View.GONE
                    findViewById<android.view.View>(R.id.clsNoData).visibility =
                        android.view.View.VISIBLE
                } else {
                    recyclerView.visibility = android.view.View.VISIBLE
                    findViewById<android.view.View>(R.id.clsNoData).visibility =
                        android.view.View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UpdateClasses", "Error loading classes data: ${error.message}")
            }
        })
    }

    private fun loadJoinedClassesData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            // Retrieve the list of joined classes for the current user
            joinedClassesRef.child(userId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val joinedClassesList = mutableListOf<JoinedClass>()

                    for (classSnapshot in snapshot.children) {
                        val classId = classSnapshot.key
                        if (classId != null) {
                            val classNameRef = FirebaseDatabase.getInstance()
                                .getReference("classes/$classId/className")

                            classNameRef.addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(classNameSnapshot: DataSnapshot) {
                                    val className = classNameSnapshot.getValue(String::class.java)
                                    if (className != null) {
                                        // Create a JoinedClass object with both class ID and class name
                                        val joinedClass = JoinedClass(classId, className)
                                        joinedClassesList.add(joinedClass)
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e(
                                        "UpdateClasses",
                                        "Error loading joined classes data: ${error.message}"
                                    )
                                }
                            })
                        }
                    }
                    // Update the RecyclerView adapter with the joined classes
                    joinedClassesAdapter.updateData(joinedClassesList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("UpdateClasses", "Error loading joined classes data: ${error.message}")
                }
            })
        }
    }
}