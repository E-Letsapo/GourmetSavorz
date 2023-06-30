package com.example.gourmetsavorz

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class EmailActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.email)

        auth = FirebaseAuth.getInstance()
        val cancel = findViewById<Button>(R.id.cancel)
        val submit = findViewById<Button>(R.id.submit)
        val emailEditText = findViewById<EditText>(R.id.Email)
        val progressDialog = ProgressDialog(this)

        cancel.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        progressDialog.setMessage("Verifying password...")
        progressDialog.show()
        fun sendEmailVerification(user: FirebaseUser) {
            user.sendEmailVerification()
                .addOnCompleteListener { task ->
                    progressDialog.dismiss()
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Verification link sent to your email.",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }
                    else {
                        Toast.makeText(this,"Failed to update email",Toast.LENGTH_LONG).show()
                    }
                }
        }
        fun updateEmail(email: String) {
            val user = auth.currentUser
            user?.updateEmail(email)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sendEmailVerification(user)
                }
            }
        }
        submit.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            if (email.isNotEmpty()) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    updateEmail(email)
                } else {
                    Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show()
            }
        }

        progressDialog.dismiss()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, AccountActivity::class.java))
        finish()
    }
}