package com.example.dogadoptionapp.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.dogadoptionapp.R
import com.example.dogadoptionapp.activities.DogAppCallback

/**
 * A simple [Fragment] subclass.
 */
class ChatFragment : Fragment() {

    private var callback: DogAppCallback? = null

    fun setCallback(callback: DogAppCallback){
        this.callback = callback
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }


}
