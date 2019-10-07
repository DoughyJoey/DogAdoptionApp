package com.example.dogadoptionapp.activities

import com.google.firebase.database.DatabaseReference

interface DogAppCallback {

    fun onSignout()
    fun onGetUserId(): String
    fun getUserDatabase(): DatabaseReference
    fun profileComplete()
    fun startActivityForPhoto()


}