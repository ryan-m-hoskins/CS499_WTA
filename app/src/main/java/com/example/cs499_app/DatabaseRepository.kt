package com.example.cs499_app

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DatabaseRepository {
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val usersRef = database.getReference("users")
    private val weightRecordsRef = database.getReference("weight_records")
    private val targetWeightRef = database.getReference("target_weight")

    // == Add or change Target Weight == //
    fun updateTargetWeight(targetWeight: Double, onSuccess: () -> Unit, onError: (String) -> Unit) {
        // If the uer is not logged in or authenticated, return early
        val currentUser = auth.currentUser?.uid ?: return
        usersRef.child(currentUser).child("targetWeight").setValue(targetWeight)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError(it.message ?: "Error updating target weight")
            }
    }

    // == Retrieve the target weight from the database == //
    fun getTargetWeight(onSuccess: (Double?) -> Unit, onError: (String) -> Unit) {
        // If the user is logged in and authenticated, continue; otherwise, return early
        val currentUser = auth.currentUser?.uid ?: return
        // Reference to the target weight node in the database
        usersRef.child(currentUser).child("targetWeight")
            // get the target weight
            .get()
            .addOnSuccessListener { snapshot ->
                val targetWeight = snapshot.getValue(Double::class.java)
                onSuccess(targetWeight)
            }
            // If there is an error, notify user with error message
            .addOnFailureListener { exception ->
                onError(exception.message ?: "Failed to fetch target weight")
            }
    }

    // == Method For Real-Time Observer of Target Weight == //
    private var targetWeightListener: ValueEventListener? = null

    fun observeTargetWeight(onUpdate: (Double?) -> Unit, onError: (String) -> Unit) {
        val currentUser = auth.currentUser?.uid ?: return
        // Remove any existing listeners before adding a new one
        removeTargetWeightListener()

        // Create new listener
        targetWeightListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val targetWeight = snapshot.getValue(Double::class.java)
                onUpdate(targetWeight)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.message)
            }
        }

        usersRef.child(currentUser).child("targetWeight")
            .addValueEventListener(targetWeightListener!!)
    }

    fun removeTargetWeightListener() {
        val currentUser = auth.currentUser?.uid ?: return
        targetWeightListener?.let { listener ->
            usersRef.child(currentUser).child("targetWeight").removeEventListener(listener)
        }
        targetWeightListener = null
    }
}