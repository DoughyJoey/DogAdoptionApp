package com.example.dogadoptionapp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.TableLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.dogadoptionapp.R
import com.example.dogadoptionapp.Util.DATA_CHATS
import com.example.dogadoptionapp.Util.DATA_USERS
import com.example.dogadoptionapp.fragments.ChatFragment
import com.example.dogadoptionapp.fragments.ProfileFragment
import com.example.dogadoptionapp.fragments.SwipingFragment
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.IOException

const val REQUEST_CODE_PHOTO = 1234

class DogAppActivity : AppCompatActivity(), DogAppCallback {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userId = firebaseAuth.currentUser?.uid
    private lateinit var userDatabase: DatabaseReference
    private lateinit var chatDatabase: DatabaseReference

    /* create the 3 fragments */
    private var profileFragment: ProfileFragment? = null
    private var swipingFragment: SwipingFragment? = null
    private var chatFragment: ChatFragment? = null

    /* create the 3 tabs */
    private var profileTab: TabLayout.Tab? = null
    private var swipingTab: TabLayout.Tab? = null
    private var chatTab: TabLayout.Tab? = null

    private var resultImageUrl: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* checks for a user and sign out if there is not */
        if(userId.isNullOrEmpty()){
            onSignout()
        }

        /* gets the users and chats from the database */
        userDatabase = FirebaseDatabase.getInstance().reference.child(DATA_USERS)
        chatDatabase = FirebaseDatabase.getInstance().reference.child(DATA_CHATS)

        /* creates the 3 navigation tabs */
        profileTab = navigationTabs.newTab()
        swipingTab = navigationTabs.newTab()
        chatTab = navigationTabs.newTab()

        /* attaches the 3 icons to the tabs */
        profileTab?.icon = ContextCompat.getDrawable(this, R.drawable.tab_profile)
        swipingTab?.icon = ContextCompat.getDrawable(this, R.drawable.tab_swiping)
        chatTab?.icon = ContextCompat.getDrawable(this, R.drawable.tab_chat)

        /* assign the tabs to the navigation tabs */
        navigationTabs.addTab(profileTab!!)
        navigationTabs.addTab(swipingTab!!)
        navigationTabs.addTab(chatTab!!)


        /* when a tab is selected we call the listener */
        navigationTabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            /* redirects to onTabSelected */
            override fun onTabReselected(tab: TabLayout.Tab?) {
               onTabSelected(tab)
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            /* switch case */
            /* allows us to switch between the fragments */
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab){
                    profileTab -> {
                        if(profileFragment == null){
                            profileFragment = ProfileFragment()
                            profileFragment!!.setCallback(this@DogAppActivity)
                        }
                        replaceFragment(profileFragment!!)
                    }
                    swipingTab -> {
                        if(swipingFragment == null){
                            swipingFragment = SwipingFragment()
                            swipingFragment!!.setCallback(this@DogAppActivity)
                        }
                        replaceFragment(swipingFragment!!)
                    }
                    chatTab -> {
                        if(chatFragment == null){
                            chatFragment = ChatFragment()
                            chatFragment!!.setCallback(this@DogAppActivity)
                        }
                        replaceFragment(chatFragment!!)
                    }
                }
            }

        })
        /* profile tab is first to be selected */
        profileTab?.select()

    }

    /* fragment replacement function */
    fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PHOTO){
            resultImageUrl = data?.data
            storeImage()
        }
    }

    /* uploads the image to firebase storage */
    fun storeImage(){
        if(resultImageUrl != null && userId != null){
            val filePath = FirebaseStorage.getInstance().reference.child("profileImage").child(userId)
            /* created bitmap */
            var bitmap: Bitmap? = null
            try{
                bitmap = MediaStore.Images.Media.getBitmap(application.contentResolver, resultImageUrl)
            } catch (e: IOException){
                e.printStackTrace()
            }
            /* converts image into an array of bytes */
            val baos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 20, baos)
            val data = baos.toByteArray()

            val uploadTask = filePath.putBytes(data)
            uploadTask.addOnFailureListener{ e -> e.printStackTrace()}
            uploadTask.addOnSuccessListener { taskSnapshot ->
                filePath.downloadUrl.addOnSuccessListener { uri ->
                    profileFragment?.updateImageUri(uri.toString())
                }
                    .addOnFailureListener{ e -> e.printStackTrace()}
            }
        }
    }


    /* signs user out and sends them back to the startup activity */
    override fun onSignout() {
        firebaseAuth.signOut()
        startActivity(StartupActivity.newIntent(this))
        finish()
    }

    /* gets the user id */
    override fun onGetUserId(): String = userId!!

    /* gets the users from database */
    override fun getUserDatabase(): DatabaseReference = userDatabase

    /* gets the chats from database */
    override fun getChatDatabase(): DatabaseReference = chatDatabase

    /* redirects user to the swipe page after clicking apply on the profile page */
    override fun profileComplete() {
        swipingTab?.select()
    }

    /* returns the image */
    override fun startActivityForPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        /* tells android system to get an image of any type */
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_PHOTO)
    }

    /* how kotlin passes a class type */
    companion object{
        fun newIntent(context: Context?) = Intent(context, DogAppActivity::class.java)
    }
}

/*https://github.com/Diolor/Swipecards*/
