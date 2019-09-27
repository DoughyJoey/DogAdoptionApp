package com.example.dogadoptionapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.dogadoptionapp.R
import java.security.AccessControlContext

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)
    }

    fun onLogin(v: View){
        startActivity(MainActivity.newIntent(this))
    }

//    static function
    companion object{
        fun newIntent(context: Context?) = Intent(context, LoginActivity::class.java)
    }
}
