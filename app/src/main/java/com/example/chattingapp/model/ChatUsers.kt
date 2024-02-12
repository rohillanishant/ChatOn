package com.example.chattingapp.model

data class ChatUsers(
    val userId:String,
    val userName:String,
    val name:String,
    val lastMessage:String,
    val date:String,
    val time:String,
    val isReceived:Boolean,
    val profilePhoto:String,
    val token:String
)
