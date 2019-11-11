package com.example.dogadoptionapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.dogadoptionapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login2.*

class LoginActivity : AppCompatActivity() {


    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        /* check if the user exists */
        val user = firebaseAuth.currentUser
        if(user != null){
            startActivity(DogAppActivity.newIntent(this))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)
    }

    /* adds the listener */
    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaseAuthListener)
    }

    /* removes the listener */
    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(firebaseAuthListener)
    }


    fun onLogin(v: View){
        /* checks if we have the variables email and password */
        if(!emailET.text.toString().isNullOrEmpty() && !passwordET.text.toString().isNullOrEmpty()){
            /* instructs firebase to create the user */
            firebaseAuth.signInWithEmailAndPassword(emailET.text.toString(), passwordET.text.toString())
                .addOnCompleteListener { task ->
                    /* If the login was not successfull, print the error */
                    if(!task.isSuccessful){
                        Toast.makeText(this, "Oops ${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    /* how kotlin passes a class type */
    companion object{
        fun newIntent(context: Context?) = Intent(context, LoginActivity::class.java)
    }
}
