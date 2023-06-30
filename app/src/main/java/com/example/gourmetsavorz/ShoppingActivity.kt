package com.example.gourmetsavorz

import android.app.AlertDialog
import android.content.Intent
import android.content.IntentSender
import android.graphics.pdf.PdfDocument
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat


class ShoppingActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var shoppingRef: DatabaseReference
    private val REQUEST_CHECK_SETTINGS = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shopping)

        database = FirebaseDatabase.getInstance()
        shoppingRef = database.getReference("shopping_lists")
        val home = findViewById<ImageView>(R.id.home)
        val smart = findViewById<ImageView>(R.id.smart)
        val saved = findViewById<ImageView>(R.id.saved)
        val shopping = findViewById<ImageView>(R.id.shopping)
        val reviews = findViewById<ImageView>(R.id.reviews)
        val chef = findViewById<ImageView>(R.id.chef)
        val share = findViewById<ImageView>(R.id.share)
        val location = findViewById<ImageView>(R.id.location)

        share.setOnClickListener {
            val container = findViewById<LinearLayout>(R.id.container)

            // Check if the shopping list is empty
            if (container.childCount == 0) {
                Toast.makeText(this, "Cannot share an empty shopping list", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(container.width, container.height, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            container.draw(page.canvas)
            pdfDocument.finishPage(page)

            val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "shopping_list.pdf")
            try {
                val fileOutputStream = FileOutputStream(file)
                pdfDocument.writeTo(fileOutputStream)
                fileOutputStream.close()
                pdfDocument.close()

                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "application/pdf"
                val uri = FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.provider", file)
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                startActivity(Intent.createChooser(shareIntent, "Share Shopping List PDF"))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        location.setOnClickListener {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            // Check if the user has granted location permission
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request location permission if it hasn't been granted
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
                return@setOnClickListener
            }

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Get user's current location
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude

                        // Set the URI for Google Maps
                        val uri = Uri.parse("geo:$latitude,$longitude?q=shopping+store&radius=200")

                        // Set the intent data
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        intent.setPackage("com.google.android.apps.maps")

                        // Verify that the user has Google Maps installed
                        if (intent.resolveActivity(packageManager) != null) {
                            // Add a marker for the user's location
                            val markerUri = Uri.parse("geo:$latitude,$longitude")
                            val markerIntent = Intent(Intent.ACTION_VIEW, markerUri)
                            markerIntent.setPackage("com.google.android.apps.maps")
                            markerIntent.putExtra("query", "Your location")
                            markerIntent.putExtra("zoom", 15)
                            startActivity(markerIntent)

                            // Launch Google Maps with the shopping stores query
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Google Maps is not installed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        // If location is null, ask if the user should be taken to phone's location settings
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Location not activated")
                            .setMessage("Do you want to go to settings to activate location?")
                            .setPositiveButton("Yes") { dialog, which ->
                                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                startActivity(intent)
                            }
                            .setNegativeButton("No") { dialog, which ->
                                Toast.makeText(this, "Failed to get your location.", Toast.LENGTH_LONG)
                                    .show()
                            }
                            .show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ShoppingActivity", "Failed to get location: ${e.message}")
                    if (e is ResolvableApiException) {
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            e.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                        } catch (sendEx: IntentSender.SendIntentException) {
                            Toast.makeText(this, "Failed to get location: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        // If location is null, ask if the user should be taken to phone's location settings
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Location not activated")
                            .setMessage("Do you want to go to settings to activate location?")
                            .setPositiveButton("Yes") { dialog, which ->
                                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                startActivity(intent)
                            }
                            .setNegativeButton("No") { dialog, which ->
                                Toast.makeText(this, "Failed to get your location.", Toast.LENGTH_LONG)
                                    .show()
                            }
                            .show()
                    }
                }
        }
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

        shoppingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val container = findViewById<LinearLayout>(R.id.container)
                container.removeAllViews()

                if (dataSnapshot.exists()) {
                    for (ingredientSnapshot in dataSnapshot.children) {
                        val ingredient = ingredientSnapshot.getValue(String::class.java)

                        if (ingredient != null) {
                            val textView = TextView(this@ShoppingActivity)
                            textView.text = ingredient
                            container.addView(textView)
                        }
                    }
                } else {
                    val textView = TextView(this@ShoppingActivity)
                    textView.text = "Your shopping list is empty"
                    container.addView(textView)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error here
                Log.e("ShoppingActivity", "Database error: ${databaseError.message}")
                Toast.makeText(this@ShoppingActivity, "Failed to load your shopping list.", Toast.LENGTH_SHORT).show()
            }
        })
    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }
}

