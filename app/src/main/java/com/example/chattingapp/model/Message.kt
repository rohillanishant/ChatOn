package com.example.chattingapp.model

data class Message(
    val messageId:String,
    val message:String,
    val photo:Boolean,
    val received:Boolean,   //if msg received = true , if sent =false
    val date:String,
    val time:String
)
