package com.example.gourmetsavorz

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class AccountActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account)

        auth = FirebaseAuth.getInstance()
        val changeemail = findViewById<TextView>(R.id.change_email)
        val changepassword = findViewById<TextView>(R.id.change_password)
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)

        changeemail.setOnClickListener {
            startActivity(Intent(this, PasswordActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        changepassword.setOnClickListener {
            val builder = android.app.AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.alert, null)
            val message = view.findViewById<TextView>(R.id.message)
            message.text = "Are you sure you want to reset your password??"
            message.setTypeface(null, Typeface.BOLD)
            builder.setView(view)
            builder.setPositiveButton("YES") { _, _ ->
                progressBar.visibility = View.VISIBLE
                auth.sendPasswordResetEmail(auth.currentUser?.email.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            Toast.makeText(this@AccountActivity, "Link sent to your email", Toast.LENGTH_SHORT).show()
                            progressBar.visibility = View.GONE
                        } else {
                            Toast.makeText(this@AccountActivity, "Failed to send the link.", Toast.LENGTH_SHORT).show()
                            progressBar.visibility = View.GONE
                        }
                    }
            }
            builder.setNegativeButton("NO") { _, _ ->
            }
            builder.show()
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MoreActivity::class.java))
        finish()
    }
}