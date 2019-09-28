package com.example.dogadoptionapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.TableLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.dogadoptionapp.R
import com.example.dogadoptionapp.fragments.ChatFragment
import com.example.dogadoptionapp.fragments.ProfileFragment
import com.example.dogadoptionapp.fragments.SwipingFragment
import com.google.android.material.tabs.TabLayout
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import kotlinx.android.synthetic.main.activity_main.*

class DogAppActivity : AppCompatActivity() {

    private var profileFragment: ProfileFragment? = null
    private var swipingFragment: SwipingFragment? = null
    private var chatFragment: ChatFragment? = null

    private var profileTab: TabLayout.Tab? = null
    private var swipingTab: TabLayout.Tab? = null
    private var chatTab: TabLayout.Tab? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                        }
                        replaceFragment(profileFragment!!)
                    }
                    swipingTab -> {
                        if(swipingFragment == null){
                            swipingFragment = SwipingFragment()
                        }
                        replaceFragment(swipingFragment!!)
                    }
                    chatTab -> {
                        if(chatFragment == null){
                            chatFragment = ChatFragment()
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

    //    static function
    companion object{
        fun newIntent(context: Context?) = Intent(context, DogAppActivity::class.java)
    }
}

/*https://github.com/Diolor/Swipecards*/
