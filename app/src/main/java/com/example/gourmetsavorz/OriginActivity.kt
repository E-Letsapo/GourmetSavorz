package com.example.gourmetsavorz

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.LinearLayout

class OriginActivity : AppCompatActivity() {
    private var selectedOrigin = mutableListOf<String>()
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.origin)

        val origins = arrayOf("African", "American","British","Cajun","Caribbean","Chinese" ,"Eastern European","European" ,"French" ,"German" ,"Greek",
                "Indian" ,"Irish", "Italian" ,"Japanese" ,"Jewish" ,"Korean" ,"Latin American" ,"Mediterranean" ,"Mexican" ,"Middle Eastern" ,"Nordic",
                "Southern" ,"Spanish" ,"Thai" ,"Vietnamese")

        val originLayout = findViewById<LinearLayout>(R.id.origin_view)
        sharedPref = getSharedPreferences("SELECTED_ORIGIN", Context.MODE_PRIVATE)

        // Restore saved selected origin, if any
        selectedOrigin = sharedPref.getStringSet("SELECTED_ORIGINS", mutableSetOf())?.toMutableList() ?: mutableListOf()

        for (origin in origins) {
            val originTextView = TextView(this)
            originTextView.text = origin
            originTextView.textSize = 25f
            originTextView.setPadding(35,0,16,0)
            originTextView.setOnClickListener {
                if (selectedOrigin.contains(origin)) {
                    selectedOrigin.remove(origin)
                    originTextView.setBackgroundColor(Color.TRANSPARENT)
                } else {
                    selectedOrigin.add(origin)
                    originTextView.setBackgroundColor(Color.parseColor("#000000"))
                }
            }
            originLayout.addView(originTextView)
        }

        val nextButton = findViewById<ImageView>(R.id.next)
        nextButton.setOnClickListener {
            if (selectedOrigin.isEmpty()) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Error")
                builder.setMessage("Please select an origin or press skip")
                builder.setPositiveButton("Ok") { _, _-> }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            } else {
                // Do something with the selected origin
                with(sharedPref.edit()) {
                    putStringSet("SELECTED_ORIGIN_KEY", selectedOrigin.toSet())
                    apply()
                }
                startActivity(Intent(this, FooddrinkActivity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }
        val skip = findViewById<Button>(R.id.skip)
        skip.setOnClickListener {
            startActivity(Intent(this, FooddrinkActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }
}
