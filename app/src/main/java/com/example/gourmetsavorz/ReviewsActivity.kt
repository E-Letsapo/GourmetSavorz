package com.example.gourmetsavorz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase

class ReviewsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reviews)

        val submit = findViewById<Button>(R.id.submit)
        val cancel = findViewById<Button>(R.id.cancel)
        val review = findViewById<EditText>(R.id.Review)
        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)

        submit.setOnClickListener {
            val userReview = review.text.toString().trim()
            val userRating = ratingBar.rating

            if (userReview.isEmpty()) {
                Toast.makeText(this, "Please enter your review", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val database = FirebaseDatabase.getInstance().reference
            val review = Review(userReview, userRating)
            database.child("reviews").push().setValue(review)

            Toast.makeText(this, "Your review has been submitted", Toast.LENGTH_SHORT).show()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }

        cancel.setOnClickListener {
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
    }
}
data class Review(val review: String, val rating: Float)