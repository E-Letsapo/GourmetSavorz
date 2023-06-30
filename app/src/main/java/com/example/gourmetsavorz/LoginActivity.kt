/*WHAT DO I HAVE TO DO IN THE LOGINACTIVITY SO THAT I CHECK IF THE PASSWORD IS FROM THE LOCKED CHILD SO THAT THE USER CAN NOT USE THAT PASSWORD AND BE FORCED TO RESET THEIR PASSWORD TO UNLOCK THEIR ACCOUNT????????HERE IS THE SIGNIN FROM THE LOGINACTIVITY,MODIFY IT TO ACHIEVE THIS:*/
package com.example.gourmetsavorz

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    // Firebase authentication
    private lateinit var auth: FirebaseAuth
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        auth = FirebaseAuth.getInstance()

        val emailEditText = findViewById<EditText>(R.id.Email)
        val passwordEditText = findViewById<EditText>(R.id.Password)
        val signInButton = findViewById<Button>(R.id.login)
        val progressDialog = ProgressDialog(this)
        val forgotPassword = findViewById<TextView>(R.id.forgot)
        val visibility = findViewById<ImageView>(R.id.show_hide)

        forgotPassword.setOnClickListener {
            startActivity(Intent(this, EmaildialogActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        visibility.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                passwordEditText.transformationMethod = null
                visibility.setImageResource(R.drawable.visibility1)
            } else {
                passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                visibility.setImageResource(R.drawable.visibility)
            }
            // move the cursor to the end of the text
            passwordEditText.setSelection(passwordEditText.text.length)
        }

        signInButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressDialog.setMessage("Logging in...")
            progressDialog.show()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    progressDialog.dismiss()
                    if (task.isSuccessful) {
                        if (auth.currentUser!!.isEmailVerified) {
                            val userId = auth.currentUser!!.uid
                            val database = FirebaseDatabase.getInstance().reference

                            database.child("users").child(userId).child("email").setValue(email)
                            database.child("users").child(userId).child("password").setValue(password)

                            startActivity(Intent(this, MainActivity::class.java))
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                            finish()
                        } else {
                            Toast.makeText(this, "Email is not verified, please go and verify your email first", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        // Sign in failure
                        when (task.exception) {
                            is FirebaseAuthInvalidCredentialsException -> {
                                // Incorrect password
                                Toast.makeText(this, "Incorrect password, please enter a correct password or use forgot password to reset your password if you forgot it", Toast.LENGTH_LONG).show()
                            }
                            is FirebaseAuthInvalidUserException -> {
                                // Email not found
                                Toast.makeText(this, "Email not found, please sign up first", Toast.LENGTH_LONG).show()
                            }
                            is FirebaseAuthRecentLoginRequiredException -> {
                                // Password has been changed recently
                                Toast.makeText(this, "Password has been changed recently,please use your new password", Toast.LENGTH_LONG).show()
                            }
                            else -> {
                                // Other failure cases
                                Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, WelcomeActivity::class.java))
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }
}
