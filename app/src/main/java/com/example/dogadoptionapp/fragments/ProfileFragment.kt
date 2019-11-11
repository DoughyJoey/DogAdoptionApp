package com.example.dogadoptionapp.fragments


import android.app.Person
import android.os.Bundle
import android.os.ProxyFileDescriptorCallback
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide

import com.example.dogadoptionapp.R
import com.example.dogadoptionapp.User
import com.example.dogadoptionapp.Util.*
import com.example.dogadoptionapp.activities.DogAppCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_profile.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    // to get the userId of the current user that is logged in
    // lateinit - will be instantiated before it is ever used
    // userDatabase used to retrieve and store information on the database
    private lateinit var userId: String
    private lateinit var userDatabase: DatabaseReference
    private var callback: DogAppCallback? = null

    fun setCallback(callback: DogAppCallback){
        this.callback = callback
        userId = callback.onGetUserId()
        /* gives us the user using userId */
        userDatabase = callback.getUserDatabase().child(userId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* stops the user clicking on elements while page is loading */
        progressLayout.setOnTouchListener{view, event -> true}

        populateInformation()

        /* image on click listener */
        photoIV.setOnClickListener{ callback?.startActivityForPhoto()}

        /* sets a listener on the apply function */
        applyButton.setOnClickListener{ onApply() }
        /* sets a listener on the sign out function */
        signoutButton.setOnClickListener{ callback?.onSignout()}
    }

    fun populateInformation(){
        /* show the progress bar when populating information */
        progressLayout.visibility = View.VISIBLE
        /* gets data from database and puts them inside elements */
        userDatabase.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                progressLayout.visibility = View.GONE
            }


            override fun onDataChange(p0: DataSnapshot) {
                if(isAdded){
                    val user = p0.getValue(User::class.java)
                    nameET.setText(user?.name, TextView.BufferType.EDITABLE)
                    emailET.setText(user?.email, TextView.BufferType.EDITABLE)
                    ageET.setText(user?.age, TextView.BufferType.EDITABLE)
                    /* sets user preferences */
                    if(user?.preference == DATA_PERSON){
                        radioPerson1.isChecked = true
                    }
                    if(user?.preference == DATA_DOGGO){
                        radioDoggo1.isChecked = true
                    }
                    if(user?.preference2 == DATA_PERSON){
                         radioPerson2.isChecked = true
                    }
                    if(user?.preference2 == DATA_DOGGO){
                        radioDoggo2.isChecked = true
                    }

                    if(!user?.imageUrl.isNullOrEmpty()){
                        populateImage(user?.imageUrl!!)
                    }

                    /* removes progress bar when information has been loaded */
                    progressLayout.visibility = View.GONE
                }
            }

        })
    }

    /* sets the users profile information */
    fun onApply(){
        /* error handling */
        if(nameET.text.toString().isNullOrEmpty() ||
            emailET.text.toString().isNullOrEmpty() ||
                radioGroup1.checkedRadioButtonId == -1 ||
                radioGroup2.checkedRadioButtonId == -1){
            /* print message to the user */
            Toast.makeText(context, getString(R.string.incomplete_profile_error), Toast.LENGTH_SHORT).show()
        } else {
            val name = nameET.text.toString()
            val age = ageET.text.toString()
            val email = emailET.text.toString()
            val preference =
                if (radioPerson1.isChecked) DATA_PERSON
            else DATA_DOGGO
            val preference2 =
                if(radioPerson2.isChecked) DATA_PERSON
            else DATA_DOGGO

            /* updates information in the database */
            userDatabase.child(DATA_NAME).setValue(name)
            userDatabase.child(DATA_AGE).setValue(age)
            userDatabase.child(DATA_EMAIL).setValue(email)
            userDatabase.child(DATA_PREFERENCE).setValue(preference)
            userDatabase.child(DATA_PREFERENCE2).setValue(preference2)

            callback?.profileComplete()
        }
    }

    /* updates the profile photo */
    fun updateImageUri(uri: String){
        userDatabase.child(DATA_IMAGE_URL).setValue(uri)
        populateImage(uri)
    }

    /* loads the image */
    fun populateImage(uri: String){
        Glide.with(this)
            .load(uri)
            .into(photoIV)
    }
}
