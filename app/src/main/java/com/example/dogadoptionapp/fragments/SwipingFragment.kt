package com.example.dogadoptionapp.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast

import com.example.dogadoptionapp.R
import com.example.dogadoptionapp.User
import com.example.dogadoptionapp.Util.*
import com.example.dogadoptionapp.activities.DogAppCallback
import com.example.dogadoptionapp.adapters.CardsAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import kotlinx.android.synthetic.main.fragment_swiping.*

/**
 * A simple [Fragment] subclass.
 */
class SwipingFragment : Fragment() {

    private var callback: DogAppCallback? = null
    private lateinit var userId: String
    private lateinit var userDatabase: DatabaseReference
    private lateinit var chatDatabase: DatabaseReference
    private var cardsAdapter: ArrayAdapter<User>? = null
//  rowItems are items the adaptor will display
    private var rowItems = ArrayList<User>()

    private var preference2: String? = null
    private var userName: String? = null
    private var imageUrl: String? = null

    /* gets users from the database and matched users from the chat database*/
    fun setCallback(callback: DogAppCallback) {
        this.callback = callback
        userId = callback.onGetUserId()
        userDatabase = callback.getUserDatabase()
        chatDatabase = callback.getChatDatabase()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_swiping, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* reads information from the database */
        userDatabase.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                preference2 = user?.preference2
                userName = user?.name
                imageUrl = user?.imageUrl
                populateItems()
            }
        })

        /* create adapter and start displaying users on the main screen*/
        cardsAdapter = CardsAdapter(context, R.layout.item, rowItems)

        /* attaches the adaptor the the frame */
        frame.adapter = cardsAdapter
        frame.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {

            /* allows us to get rid of the first card */
            override fun removeFirstObjectInAdapter() {
                rowItems.removeAt(0)
                cardsAdapter?.notifyDataSetChanged()
            }

            /* swipes left functionality */
            /* sets DATA_SWIPE_LEFT to users id */
            override fun onLeftCardExit(p0: Any?) {
                var user = p0 as User
                userDatabase.child(user.uid.toString()).child(DATA_SWIPE_LEFT).child(userId).setValue(true)
            }

            /* swipes right functionality */
            /* sets DATA_SWIPE_RIGHT to users id */
            override fun onRightCardExit(p0: Any?) {
                val selectedUser = p0 as User
                val selectedUserId = selectedUser.uid
                if (!selectedUserId.isNullOrEmpty()) {
                    userDatabase.child(userId).child(DATA_SWIPE_RIGHT)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                            }

                            /* if both users swipe right on each other and have matched */
                            override fun onDataChange(p0: DataSnapshot) {
                                if (p0.hasChild(selectedUserId)) {
                                    /* prints message notifying user of a match */
                                    Toast.makeText(context, "Match!", Toast.LENGTH_SHORT).show()


                                    val chatKey = chatDatabase.push().key


                                    if (chatKey != null) {
                                        /* if matched, user id is removed from swipeRight and placed in the matches */
                                        userDatabase.child(userId).child(DATA_SWIPE_RIGHT).child(selectedUserId)
                                            .removeValue()
                                        userDatabase.child(userId).child(DATA_MATCHES).child(selectedUserId)
                                            .setValue(chatKey)
                                        userDatabase.child(selectedUserId).child(DATA_MATCHES).child(userId)
                                            .setValue(chatKey)

                                        chatDatabase.child(chatKey).child(userId).child(DATA_NAME).setValue(userName)
                                        chatDatabase.child(chatKey).child(userId).child(DATA_IMAGE_URL)
                                            .setValue(imageUrl)

                                        chatDatabase.child(chatKey).child(selectedUserId).child(DATA_NAME)
                                            .setValue(selectedUser.name)
                                        chatDatabase.child(chatKey).child(selectedUserId).child(DATA_IMAGE_URL)
                                            .setValue(selectedUser.imageUrl)
                                    }
                                } else {
                                    /* userId is set to swipeRight */
                                    userDatabase.child(selectedUserId).child(DATA_SWIPE_RIGHT).child(userId)
                                        .setValue(true)
                                }
                            }
                        })
                }
            }

            override fun onAdapterAboutToEmpty(p0: Int) {
            }

            override fun onScroll(p0: Float) {
            }
        })

        /* makes sure click does nothing */
        frame.setOnItemClickListener { position, data -> }

        /* LIKE BUTTON */
        likeButton.setOnClickListener {
            if (!rowItems.isEmpty()) {
                frame.topCardListener.selectRight()
            }
        }

        /* DISLIKE BUTTON */
        dislikeButton.setOnClickListener {
            if (!rowItems.isEmpty()) {
                frame.topCardListener.selectLeft()
            }
        }
    }

    /* only selects users based on their preferences */
    /* i.e - person prefers a dog or dog prefers a person*/
    fun populateItems() {
        /* displays depending on if there are users or not */
        noUsersLayout.visibility = View.GONE
        progressLayout.visibility = View.VISIBLE

        /* queries the database */
        /* gets users based on their preference */
        val cardsQuery = userDatabase.orderByChild(DATA_PREFERENCE).equalTo(preference2)
        cardsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            /* loops through the list of users */
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach { child ->
                    val user = child.getValue(User::class.java)
                    if (user != null) {
                        var showUser = true
                        /* if a user has matched, swiped left or right on another user, we do not display that user */
                        if (child.child(DATA_SWIPE_LEFT).hasChild(userId) ||
                            child.child(DATA_SWIPE_RIGHT).hasChild(userId) ||
                            child.child(DATA_MATCHES).hasChild(userId)
                        ) {
                            showUser = false
                        }
                        /* if the above conditions are not true, we display the users */
                        /* lets the adapter know if has been updated */
                        if (showUser) {
                            rowItems.add(user)
                            cardsAdapter?.notifyDataSetChanged()
                        }
                    }
                }
                progressLayout.visibility = View.GONE
                /* if there are no users, show the no users layout */
                if (rowItems.isEmpty()) {
                    noUsersLayout.visibility = View.VISIBLE
                }
            }
        })
    }

}