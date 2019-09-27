package com.example.dogadoptionapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.dogadoptionapp.R

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
    }

    fun onSignup(v: View){
        startActivity(MainActivity.newIntent(this))
    }

    //    static function
    companion object{
        fun newIntent(context: Context?) = Intent(context, SignupActivity::class.java)
    }
}
