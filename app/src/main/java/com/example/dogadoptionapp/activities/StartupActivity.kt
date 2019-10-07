package com.example.dogadoptionapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.dogadoptionapp.R

class StartupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)
    }
    /*when you click this button, start the LoginActivity with the intent provided*/
    /*connects buttons to activities*/
    fun onLogin(v: View){
        startActivity(LoginActivity.newIntent(this))
    }

    /*when you click this button, start the LoginActivity with the intent provided*/
    /*connects buttons to activities*/
    fun onSignup(v: View){
        startActivity(SignupActivity.newIntent(this))
    }

    companion object{
        fun newIntent(context: Context?) = Intent(context, StartupActivity::class.java)
    }
}
