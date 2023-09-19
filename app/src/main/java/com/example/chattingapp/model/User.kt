package com.example.chattingapp.model

import android.net.Uri

data class User(
    val userId:String,
    val userName:String,
    val profilePhoto:String,
    val name:String,
    val email:String,
    val phoneNumber:String
) {
}