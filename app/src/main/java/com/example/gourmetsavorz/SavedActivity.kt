package com.example.gourmetsavorz

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SavedActivity : AppCompatActivity() {

    private lateinit var scrollViewContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.saved)

        scrollViewContainer = findViewById(R.id.linear)
        val home = findViewById<ImageView>(R.id.home)
        val smart = findViewById<ImageView>(R.id.smart)
        val saved = findViewById<ImageView>(R.id.saved)
        val shopping = findViewById<ImageView>(R.id.shopping)
        val reviews = findViewById<ImageView>(R.id.reviews)
        val chef = findViewById<ImageView>(R.id.chef)

        home.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        smart.setOnClickListener {
            startActivity(Intent(this, OriginActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        saved.setOnClickListener {
            startActivity(Intent(this, SavedActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        shopping.setOnClickListener {
            startActivity(Intent(this, ShoppingActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        reviews.setOnClickListener {
            startActivity(Intent(this, ReviewsActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        chef.setOnClickListener {
            startActivity(Intent(this, ChefsActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val database = FirebaseDatabase.getInstance()
            val savedRecipesRef = database.getReference("users/${currentUser.uid}/savedRecipes")

            savedRecipesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (recipeSnapshot in dataSnapshot.children) {
                            val recipeTitle = recipeSnapshot.child("title").value as String
                            val recipeTextView = TextView(this@SavedActivity)
                            recipeTextView.text = recipeTitle
                            scrollViewContainer.addView(recipeTextView)
                        }
                    } else {
                        val noRecipesTextView = TextView(this@SavedActivity)
                        noRecipesTextView.text = "No saved recipes found"
                        scrollViewContainer.addView(noRecipesTextView)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle the error here
                    Log.e("SavedActivity", "Database error: ${databaseError.message}")
                    Toast.makeText(this@SavedActivity, "Failed to load saved recipes.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }
}