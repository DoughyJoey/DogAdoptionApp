package com.example.dogadoptionapp

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