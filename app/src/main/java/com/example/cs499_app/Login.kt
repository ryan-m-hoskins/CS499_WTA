package com.example.cs499_app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider

class Login : AppCompatActivity() {
    private lateinit var loginViewModel : LoginViewModel

    private var usernameEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private var loginButton: Button? = null
    private var signupButton: Button? = null
    private var loginErrorMessage: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize the fields used in the login screen
        usernameEditText = findViewById(R.id.usernameEditText)
        loginButton = findViewById(R.id.loginButton)
        passwordEditText = findViewById(R.id.passwordEditText)
        signupButton = findViewById(R.id.signupButton)
        loginErrorMessage = findViewById(R.id.loginErrorMessage)


        // === Disable Sign In if username and password are empty === //
        usernameEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                // Nothing
            }
            // Call checkFields when usernameEditText has changed
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                checkFields()
            }
            override fun afterTextChanged(editable: Editable) {
                // Nothing
            }
        });
    }

    // Inside your LoginActivity or a helper function
    fun checkFields() {
        val username = usernameEditText?.text?.toString()?.trim() // Get the username text
        val password = passwordEditText?.text?.toString()?.trim() // Get the password text
        loginButton?.isEnabled = !username.isNullOrEmpty() && !password.isNullOrEmpty() // Enable if both are filled

    }

}

