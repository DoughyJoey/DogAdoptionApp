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

    private var profileFragment: ProfileFragment? = null
    private var swipingFragment: SwipingFragment? = null
    private var chatFragment: ChatFragment? = null

    private var profileTab: TabLayout.Tab? = null
    private var swipingTab: TabLayout.Tab? = null
    private var chatTab: TabLayout.Tab? = null

    private var resultImageUrl: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(userId.isNullOrEmpty()){
            onSignout()
        }

        userDatabase = FirebaseDatabase.getInstance().reference.child(DATA_USERS)
        chatDatabase = FirebaseDatabase.getInstance().reference.child(DATA_CHATS)

        profileTab = navigationTabs.newTab()
        swipingTab = navigationTabs.newTab()
        chatTab = navigationTabs.newTab()

        profileTab?.icon = ContextCompat.getDrawable(this, R.drawable.tab_profile)
        swipingTab?.icon = ContextCompat.getDrawable(this, R.drawable.tab_swiping)
        chatTab?.icon = ContextCompat.getDrawable(this, R.drawable.tab_chat)

        navigationTabs.addTab(profileTab!!)
        navigationTabs.addTab(swipingTab!!)
        navigationTabs.addTab(chatTab!!)

        navigationTabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
               onTabSelected(tab)
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

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
        profileTab?.select()

    }

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

    fun storeImage(){
        if(resultImageUrl != null && userId != null){
            val filePath = FirebaseStorage.getInstance().reference.child("profileImage").child(userId)
            var bitmap: Bitmap? = null
            try{
                bitmap = MediaStore.Images.Media.getBitmap(application.contentResolver, resultImageUrl)
            } catch (e: IOException){
                e.printStackTrace()
            }
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

    override fun onSignout() {
        firebaseAuth.signOut()
        startActivity(StartupActivity.newIntent(this))
        finish()
    }

    override fun onGetUserId(): String = userId!!

    override fun getUserDatabase(): DatabaseReference = userDatabase

    override fun getChatDatabase(): DatabaseReference = chatDatabase

    override fun profileComplete() {
        swipingTab?.select()
    }

    override fun startActivityForPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_PHOTO)
    }

    //    static function
    companion object{
        fun newIntent(context: Context?) = Intent(context, DogAppActivity::class.java)
    }
}

/*https://github.com/Diolor/Swipecards*/
