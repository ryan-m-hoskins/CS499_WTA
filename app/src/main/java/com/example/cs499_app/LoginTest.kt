package com.example.cs499_app

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginTest : AppCompatActivity() {
    private val RC_SIGN_IN = 123
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Check if user is signed in
        if (auth.currentUser == null) {
            // Create sign-in intent
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build()
            )

            // Create and launch sign-in intent
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                RC_SIGN_IN
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                // Navigate to your app's main content
                // startActivity(Intent(this, HomeActivity::class.java))
                Toast.makeText(this, "Signed in successfully", Toast.LENGTH_SHORT).show()
            } else {
                // Sign in failed
                response?.error?.let {
                    Toast.makeText(this, "Sign in failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Optional: Sign out method
    private fun signOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                // User is now signed out
                // You might want to navigate back to login screen or update UI
            }
    }
}