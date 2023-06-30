package com.example.gourmetsavorz

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class ResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.results)

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
    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }
}