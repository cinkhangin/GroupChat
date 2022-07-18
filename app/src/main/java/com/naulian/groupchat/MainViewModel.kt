package com.naulian.groupchat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val messages = MutableStateFlow(arrayListOf<Message>())
    val messageFlow = messages.asStateFlow()

    private val user = MutableStateFlow(User())
    val userFlow = user.asStateFlow()


    val messageListMap = sortedMapOf<String, Message>()

    val messageListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            snapshot.getValue<Message>()?.let { message ->
                val id = snapshot.key.toString()
                messageListMap[id] = message
                message.messageId = id
                val myId = Firebase.auth.currentUser?.uid ?: ""

                if(message.id != myId) message.seen()

                val messageList = ArrayList(messageListMap.values)
                messages.value = messageList
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            snapshot.getValue<Message>()?.let { message ->
                val id = snapshot.key.toString()
                messageListMap[id] = message
                message.messageId = id
                val messageList = ArrayList(messageListMap.values)
                messages.value = messageList
            }
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            snapshot.getValue<Message>()?.let { message ->
                val id = snapshot.key.toString()
                messageListMap.remove(id)
                val messageList = ArrayList(messageListMap.values)
                messages.value = messageList
            }
        }
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) {}
    }

    val userListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            snapshot.getValue<User>()?.let {
                val id = snapshot.key.toString()
                it.userId = id
                user.value = it
            }
        }

        override fun onCancelled(error: DatabaseError) {}
    }




    fun getMessage(){
        Firebase.database.getReference("group_chat")
            .addChildEventListener(messageListener)
    }

    fun getUser(){
        val id = Firebase.auth.currentUser?.uid ?: ""
        Firebase.database.getReference("user")
            .child(id)
            .addValueEventListener(userListener)
    }
}