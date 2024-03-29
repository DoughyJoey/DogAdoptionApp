package com.example.dogadoptionapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.dogadoptionapp.R
import com.example.dogadoptionapp.User
import com.example.dogadoptionapp.activities.UserInformationActivity

/* CardsAdapter is used to create the cards on the main screen */

/* resource id is the layout of the cards*/
/* users is the list of elements that have to be displayed */
class CardsAdapter(context: Context?, resourceId: Int, users: List<User>): ArrayAdapter<User>(context, resourceId, users) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var user = getItem(position)
        /* takes the convertview if its available. otherwise, we inflate one */
        var finalView = convertView ?: LayoutInflater.from(context).inflate(R.layout.item, parent, false)

        /* sets the users photo and information layouts*/
        var name = finalView.findViewById<TextView>(R.id.nameTV)
        var image = finalView.findViewById<ImageView>(R.id.photoIV)
        var userInfo = finalView.findViewById<LinearLayout>(R.id.userInformationLayout)

        /* writes the text and loads the image */
        name.text = "${user.name}, ${user.age}"
        Glide.with(context)
            .load(user.imageUrl)
            .into(image)

        /* displays the user information page based on the userId */
        userInfo.setOnClickListener {
            finalView.context.startActivity(UserInformationActivity.newIntent(finalView.context, user.uid))
        }

        /* returns the final view */
        return finalView
    }
}