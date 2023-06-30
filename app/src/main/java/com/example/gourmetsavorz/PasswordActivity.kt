package com.example.gourmetsavorz

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class PasswordActivity : AppCompatActivity() {
    // Firebase authentication
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.password)

        auth = FirebaseAuth.getInstance()

        val cancel = findViewById<Button>(R.id.cancel)
        val submit = findViewById<Button>(R.id.submit)
        val visibility = findViewById<ImageView>(R.id.show_hide)
        val passwordEditText = findViewById<EditText>(R.id.Password)
        val progressDialog = ProgressDialog(this)
        var isPasswordVisible = false
        passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
        visibility.setImageResource(R.drawable.visibility)

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

        cancel.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }

        val sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        var attempts = sharedPreferences.getInt("attempts", 3)

        submit.setOnClickListener {
            val password = passwordEditText.text.toString()
            if (password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Password field cannot be empty. Please enter your password.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            progressDialog.setMessage("Verifying password...")
            progressDialog.show()
            val currentUser = auth.currentUser
            if(currentUser != null) {
                val email = currentUser.email
                if(email != null) {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            progressDialog.dismiss()
                            if(task.isSuccessful) {
                                attempts = 3
                                val editor = sharedPreferences.edit()
                                editor.putInt("attempts", attempts)
                                editor.apply()
                                startActivity(Intent(this,EmailActivity::class.java))
                            }else {
                                if(attempts > 1) {
                                    attempts--
                                    val editor = sharedPreferences.edit()
                                    editor.putInt("attempts", attempts)
                                    editor.apply()
                                    val builder = android.app.AlertDialog.Builder(this)
                                    val view = layoutInflater.inflate(R.layout.alert, null)
                                    val message = view.findViewById<TextView>(R.id.message)
                                    message.text = "Incorrect password. You have $attempts attempts left."
                                    message.setTypeface(null, Typeface.BOLD)
                                    builder.setView(view)
                                    builder.setPositiveButton("OK") { _, _ ->
                                        startActivity(Intent(this,PasswordActivity::class.java))
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                                    }
                                    builder.show()
                                } else {
                                    if (attempts == 1) {
                                        val builder = android.app.AlertDialog.Builder(this)
                                        val view = layoutInflater.inflate(R.layout.alert, null)
                                        val message = view.findViewById<TextView>(R.id.message)
                                        message.text = "Signing you out....."
                                        message.setTypeface(null, Typeface.BOLD)
                                        builder.setView(view)
                                        builder.show()
                                        startActivity(Intent(this, LoginActivity::class.java))
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                                    }
                                }
                            }
                        }
                }
            }
        }
        progressDialog.dismiss()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, AccountActivity::class.java))
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }
}