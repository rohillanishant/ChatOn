package com.example.chattingapp.model

data class forwardUsers(
    val userId:String,
    val userName:String,
    val profilePhoto:String,
    val name:String,
    val email:String,
    val phoneNumber:String,
    var isSelected:Boolean,
    val token:String
) {
}