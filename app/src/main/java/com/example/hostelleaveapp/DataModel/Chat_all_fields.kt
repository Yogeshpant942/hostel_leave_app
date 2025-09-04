package com.example.hostelleaveapp.DataModel

data class Chat_all_fields(
    val name:String,
    val image:String,
    val content:String,
    val receiverId:String,
    val timestamp:Long= 0L
)