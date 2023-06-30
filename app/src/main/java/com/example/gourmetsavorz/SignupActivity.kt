package com.example.gourmetsavorz

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var isPasswordVisible = false
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        // Configure Google Sign In inside onCreate mentod
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        // getting the value of gso inside the GoogleSigninClient
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = FirebaseAuth.getInstance()

        val firstname = findViewById<EditText>(R.id.fname)
        val lastname = findViewById<EditText>(R.id.lname)
        val emailEditText = findViewById<EditText>(R.id.Email)
        val passwordEditText = findViewById<EditText>(R.id.Password)
        val confirmPasswordEditText = findViewById<EditText>(R.id.cPassword)
        val hideShowPasswordImageView = findViewById<ImageView>(R.id.show_hide)
        val google = findViewById<Button>(R.id.google)
        val progressDialog = ProgressDialog(this)

        hideShowPasswordImageView.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                passwordEditText.transformationMethod = null
                confirmPasswordEditText.transformationMethod = null
                hideShowPasswordImageView.setImageResource(R.drawable.visibility1)
            } else {
                passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                confirmPasswordEditText.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                hideShowPasswordImageView.setImageResource(R.drawable.visibility)
            }
            // move the cursor to the end of the text
            passwordEditText.setSelection(passwordEditText.text.length)
            confirmPasswordEditText.setSelection(confirmPasswordEditText.text.length)
        }
        val createAccountButton = findViewById<Button>(R.id.create)
        createAccountButton.setOnClickListener {
            val firstName = firstname.text.toString()
            val lastName = lastname.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()
            val requiredLength = 8

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(
                    this,
                    "Password and Confirm Password do not match.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (!isPasswordStrong(password)) {
                if (password.length < requiredLength) {
                    Toast.makeText(
                        this,
                        "Password must be at least $requiredLength characters long.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val errorMessage = mutableListOf<String>()
                    if (!Regex("[A-Z]").containsMatchIn(password)) {
                        errorMessage.add("uppercase letters")
                    }
                    if (!Regex("[a-z]").containsMatchIn(password)) {
                        errorMessage.add("lowercase letters")
                    }
                    if (!Regex("[0-9]").containsMatchIn(password)) {
                        errorMessage.add("numbers")
                    }
                    if (!Regex("[^A-Za-z0-9]").containsMatchIn(password)) {
                        errorMessage.add("special characters")
                    }
                    val errorText = "Password must contain ${errorMessage.joinToString(", ")}."

                    Toast.makeText(
                        this,
                        errorText,
                        Toast.LENGTH_LONG
                    ).show()
                }
                return@setOnClickListener
            }
            progressDialog.setMessage("Creating Account...")
            progressDialog.show()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    progressDialog.dismiss()
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val userId = user!!.uid
                        val firebaseDatabase = FirebaseDatabase.getInstance()
                        val reference = firebaseDatabase.getReference("user_info")
                        val userData = hashMapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "email" to email,
                            "password" to password
                        )
                        reference.child(userId).push().setValue(userData)
                            .addOnSuccessListener {
                                // data successfully written to the database
                                Toast.makeText(this, "user data saved", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                // error writing data to the database
                                Toast.makeText(this, "failed to save user data", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        auth.currentUser?.sendEmailVerification()
                            ?.addOnCompleteListener {
                                progressDialog.dismiss()
                                Toast.makeText(
                                    this,
                                    "Verification link sent to your email.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(Intent(this, LoginActivity::class.java))
                                overridePendingTransition(
                                    R.anim.slide_in_right,
                                    R.anim.slide_out_left
                                )
                                finish()
                            }
                    } else {
                        Toast.makeText(this, "Failed to create account.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        google.setOnClickListener{ view: View? ->
            signInGoogle()
        }
    }
    private fun isPasswordStrong(password: String): Boolean {
        val requiredLength = 8
        val uppercaseRegex = Regex("[A-Z]")
        val lowercaseRegex = Regex("[a-z]")
        val digitRegex = Regex("[0-9]")
        val specialCharRegex = Regex("[^A-Za-z0-9]")

        return password.length >= requiredLength
                && uppercaseRegex.containsMatchIn(password)
                && lowercaseRegex.containsMatchIn(password)
                && digitRegex.containsMatchIn(password)
                && specialCharRegex.containsMatchIn(password)
    }

    private fun signInGoogle()
    {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // onActivityResult() function : this is where we provide the task and data for the Google Account

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == RC_SIGN_IN) {
                val task:Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
                firebaseAuthWithGoogle(task)
            }
        }

    // firebaseAuthWithGoogle() function -  this is where we update the UI after Google signin takes place
    private fun firebaseAuthWithGoogle(completedTask: Task<GoogleSignInAccount>){
        try {
            val account: GoogleSignInAccount? =completedTask.getResult(ApiException::class.java)
            if (account != null) {
                updateUI(account)
            }
        } catch (e:ApiException){
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show()
        }
    }
    // UpdateUI() function - this is where we specify what UI updation are needed after google signin has taken place.
    private fun updateUI(account: GoogleSignInAccount){
        val credential= GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(credential).addOnCompleteListener {task->
            if(task.isSuccessful) {
                SavedPreference.setEmail(this,account.email.toString())
                SavedPreference.setUsername(this,account.displayName.toString())
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
    override fun onStart() {
        super.onStart()
        if(GoogleSignIn.getLastSignedInAccount(this)!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, LoginActivity::class.java))
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }
}

