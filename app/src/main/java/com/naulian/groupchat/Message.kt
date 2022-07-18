package com.naulian.groupchat

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.Exclude
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

data class Message(
    val id : String = Firebase.auth.currentUser?.uid ?: "",

    @Exclude
    var messageId : String = "",

    val name : String = "",
    val text : String =  "",
    val seen : Boolean = false
){
    fun seen(){
        Firebase.database.getReference("group_chat")
            .child(messageId)
            .child("seen")
            .setValue(true)
    }

}
