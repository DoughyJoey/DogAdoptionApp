package com.example.dogadoptionapp.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dogadoptionapp.Chat

import com.example.dogadoptionapp.R
import com.example.dogadoptionapp.User
import com.example.dogadoptionapp.Util.DATA_CHATS
import com.example.dogadoptionapp.Util.DATA_MATCHES
import com.example.dogadoptionapp.activities.DogAppCallback
import com.example.dogadoptionapp.adapters.ChatsAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_chat.*

/**
 * A simple [Fragment] subclass.
 */
class ChatFragment : Fragment() {

    private lateinit var userId: String
    private lateinit var userDatabase: DatabaseReference
    private lateinit var chatDatabase: DatabaseReference
    private var callback: DogAppCallback? = null

    private val chatsAdapter = ChatsAdapter(ArrayList())

    fun setCallback(callback: DogAppCallback) {
        this.callback = callback
        userId = callback.onGetUserId()
        userDatabase = callback.getUserDatabase()
        chatDatabase = callback.getChatDatabase()

        fetchData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* applies to chatRV all the functions we call */
        chatRV.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context)
            adapter = chatsAdapter
        }
    }

    fun fetchData() {
        /* gets all the users matches */
        userDatabase.child(userId).child(DATA_MATCHES).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            /* transforms list of matches into chat elements */
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.hasChildren()) {
                    p0.children.forEach { child ->
                        val matchId = child.key
                        val chatId = child.value.toString()
                        if(!matchId.isNullOrEmpty()) {
                            /* get the data from the user database */
                            userDatabase.child(matchId).addListenerForSingleValueEvent(object: ValueEventListener{
                                override fun onCancelled(p0: DatabaseError) {
                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    val user = p0.getValue(User::class.java)
                                    if(user != null) {
                                        val chat = Chat(userId, chatId, user.uid, user.name, user.imageUrl)
                                        /* update the chat adapter */
                                        chatsAdapter.addElement(chat)
                                    }
                                }

                            })
                        }
                    }
                }
            }

        })
    }

}