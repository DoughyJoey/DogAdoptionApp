package com.example.dogadoptionapp.activities

import com.google.firebase.database.DatabaseReference

/* callback interface used when we need some information */

interface DogAppCallback {

    fun onSignout()
    fun onGetUserId(): String
    fun getUserDatabase(): DatabaseReference
    fun getChatDatabase(): DatabaseReference
    fun profileComplete()
    fun startActivityForPhoto()


}