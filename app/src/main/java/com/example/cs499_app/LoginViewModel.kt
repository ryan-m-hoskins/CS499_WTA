package com.example.cs499_app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class LoginViewModel : ViewModel() {
    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> get() = _user

    private val auth = FirebaseAuth.getInstance()

    fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _user.value = auth.currentUser
                }
                else {
                    _user.value = null
                }
            }
    }

    fun signOut() {
        auth.signOut()
        _user.value = null
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
    private val db = FirebaseDatabase.getInstance()
}