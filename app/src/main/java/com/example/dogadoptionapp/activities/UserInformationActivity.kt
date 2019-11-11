package com.example.dogadoptionapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.dogadoptionapp.R
import com.example.dogadoptionapp.User
import com.example.dogadoptionapp.Util.DATA_USERS
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_user_information.*

class UserInformationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_information)

        val userId = intent.extras.getString(PARAM_USER_ID, "")
        if(userId.isNullOrEmpty()) {
            finish()
        }

        /* get the users database */
        val userDatabase = FirebaseDatabase.getInstance().reference.child(DATA_USERS)
        userDatabase.child(userId).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            /* populates the views with user name, age, and image */
            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                userInfoName.text = user?.name
                userInfoAge.text = user?.age
                /* checks if the image is present */
                /* if it is, it gets loaded into the user information image view */
                if(user?.imageUrl != null) {
                    Glide.with(this@UserInformationActivity)
                        .load(user.imageUrl)
                        .into(userInfoIV)
                }
            }
        })
    }

    /* starts the activity */
    companion object {
        /* allows us to display the user information */
        private val PARAM_USER_ID = "User id"

        fun newIntent(context: Context, userId: String?): Intent {
            val intent = Intent(context, UserInformationActivity::class.java)
            intent.putExtra(PARAM_USER_ID, userId)
            return intent
        }
    }
}