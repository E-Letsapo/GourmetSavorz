package com.example.gourmetsavorz

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    // declare the GoogleSignInClient
    private lateinit var googleSignInClient: GoogleSignInClient

    private val requiredPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_NOTIFICATION_POLICY,
        Manifest.permission.INTERNET
    )
    private val REQUEST_CODE_PERMISSIONS = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        database = FirebaseDatabase.getInstance().reference

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient= GoogleSignIn.getClient(this,gso)

        val settings = findViewById<ImageView>(R.id.settings)
        val allrecipes = findViewById<TextView>(R.id.recipes)
        val signout = findViewById<ImageView>(R.id.sign_out)
        val smart = findViewById<ImageView>(R.id.smart)
        val saved = findViewById<ImageView>(R.id.saved)
        val shopping = findViewById<ImageView>(R.id.shopping)
        val reviews = findViewById<ImageView>(R.id.reviews)
        val home = findViewById<ImageView>(R.id.home)
        val chef = findViewById<ImageView>(R.id.chef)

        home.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, requiredPermissions, REQUEST_CODE_PERMISSIONS)
        }

        settings.setOnClickListener {
            startActivity(Intent(this, MoreActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        allrecipes.setOnClickListener {
            startActivity(Intent(this, AllrecipesActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        signout.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to sign out?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    googleSignInClient.signOut()
                    val userId = auth.currentUser!!.uid
                    val database = FirebaseDatabase.getInstance().reference

                    database.child("users").child(userId).child("email").removeValue()
                    database.child("users").child(userId).child("password").removeValue()
                    logout()
                    auth.signOut()
                    finishAffinity()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.cancel()
                }
            val alert = builder.create()
            alert.show()
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
        val share = findViewById<ImageView>(R.id.share)
        share.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Check out this amazing app!")
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }
    override fun onStop() {
        super.onStop()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            database.child("sessions").child(userId).setValue(false)
        }
    }

    // Add a logout button or similar to trigger this method
    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        finishAffinity()
    }
    // val auth is initialized by lazy
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private fun allPermissionsGranted() : Boolean {
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
    
    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.alert, null)
        val message = view.findViewById<TextView>(R.id.message)
        message.text = "Are you sure you want to close Gourmet Savorz?"
        message.setTypeface(null, Typeface.BOLD)
        builder.setView(view)
        builder.setPositiveButton("Yes") { _, _ ->
            finishAffinity()
        }
        builder.setNegativeButton("No") { _, _ ->
            // do nothing, stay in the main activity
        }
        builder.show()
    }
}