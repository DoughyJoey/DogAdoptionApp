package com.example.dogadoptionapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.dogadoptionapp.R
import com.example.dogadoptionapp.User
import com.example.dogadoptionapp.Util.DATA_USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_signup.*



class SignupActivity : AppCompatActivity() {

    /* reference to the database service */
    private val firebaseDatabase = FirebaseDatabase.getInstance().reference
    private val firebaseAuth = FirebaseAuth.getInstance()
    /* listens to the state of the firebase auth */
    /* gets called when the user is created and auth */
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        /* check if the user exists */
        val user = firebaseAuth.currentUser
        if(user != null) {
            startActivity(DogAppActivity.newIntent(this))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
    }

    /* adds the listener */
    override fun onStart() {
        super.onStart()
        /* get registered here */
        firebaseAuth.addAuthStateListener(firebaseAuthListener)
    }

    /* removes the listener */
    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(firebaseAuthListener)
    }


    fun onSignup(v: View) {
        /* checks if we have the variables email and password */
        if(!emailET.text.toString().isNullOrEmpty() && !passwordET.text.toString().isNullOrEmpty()) {
            /* instructs firebase to create the user */
            firebaseAuth.createUserWithEmailAndPassword(emailET.text.toString(), passwordET.text.toString())
                /* checks if task has been successfully completed */
                .addOnCompleteListener { task ->
                    if(!task.isSuccessful) {
                        /* notifies the user */
                        Toast.makeText(this, "Sign Up Error ${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                    } else {
                        /* users information is added to the database */
                        val email = emailET.text.toString()
                        val userId = firebaseAuth.currentUser?.uid ?: ""
                        val user = User(userId, "", "", email, "", "", "")
                        firebaseDatabase.child(DATA_USERS).child(userId).setValue(user)
                    }
                }
        }
    }

    /* how kotlin passes a class type */
    companion object {
        fun newIntent(context: Context?) = Intent(context, SignupActivity::class.java)
    }
}
