package com.example.cs499_app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    // Expose login state as a read-only Flow
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> get() = _loginState
    private val auth = FirebaseAuth.getInstance()

    // == Sign in method ==//
    fun signIn(email: String, password: String) {

        // Ensure user enters email and password
        /*
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Email and password cannot be empty")
            return
        }
         */
        // Pending login state
        _loginState.value = LoginState.Loading
        // Launch a coroutine to handle the login process
        viewModelScope.launch {
            // Use email and password to handle sign in method from Firebase Auth
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                // If able to sign in, update login state to successful
                if (task.isSuccessful) {
                    _loginState.value = LoginState.Success(auth.currentUser)
                }
                // Otherwise, output error message
                else {
                    _loginState.value = LoginState.Error(task.exception?.message?: "Unable to login")
                }
            }
        }
    }

    fun register(email: String, password: String) {
        // Pending login state
        _loginState.value = LoginState.Loading
        // Launch a coroutine to handle the login process
        viewModelScope.launch {
            // Use email and password to handle sign in method from Firebase Auth
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                // If able to sign in, update login state to successful
                if (task.isSuccessful) {
                    _loginState.value = LoginState.Success(auth.currentUser)
                }
                // Otherwise, output error message
                else {
                    _loginState.value = LoginState.Error(task.exception?.message?: "Unable to login")
                }
            }
        }
    }
    // Logic for handling login state
    sealed class LoginState {
        data object Idle : LoginState()
        data object Loading : LoginState()
        data class Success(val user: FirebaseUser?) : LoginState()
        data class Error(val message: String) : LoginState()
    }
}