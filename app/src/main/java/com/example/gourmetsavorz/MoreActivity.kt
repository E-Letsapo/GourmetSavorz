package com.example.gourmetsavorz

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.more)

        val account = findViewById<TextView>(R.id.account)
        account.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        val updates = findViewById<TextView>(R.id.updates)
        updates.setOnClickListener {
            val appPackageName = packageName // getPackageName() from Context or Activity object
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$appPackageName")
                    )
                )
            } catch (anfe: android.content.ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }
        }
        val about = findViewById<TextView>(R.id.About)
        about.setOnClickListener {
            //TODO:  About the app
        }

        val terms = findViewById<TextView>(R.id.terms)
        terms.setOnClickListener {
            //TODO: Terms and conditions for the app
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}