package com.example.dogadoptionapp

/* Data structures for Users, Chats, and Messages*/

data class User(
    val uid: String? = "",
    val name: String? = "",
    val age: String? = "",
    val email: String? = "",
    val preference: String? = "",
    val preference2: String? = "",
    val imageUrl: String? = ""
)

data class Chat(
    val userId: String? = "",
    val chatId: String? = "",
    val otherUserId: String? = "",
    val name: String? = "",
    val imageUrl: String? = ""
)

data class Message(
    val sentBy: String? = null,
    val message: String? = null,
    val time: String? = null
)