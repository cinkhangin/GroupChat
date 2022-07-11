package com.naulian.groupchat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.naulian.groupchat.databinding.FragmentMainBinding

class MainFragment : Fragment(R.layout.fragment_main) {
private lateinit var viewBinding: FragmentMainBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentMainBinding.bind(view)

        viewBinding.apply {
            topAppBar.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.action_logout -> {
                        Firebase.auth.signOut()
                        Toast.makeText(requireContext(), "sign out", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_mainFragment_to_firstFragment)
                        true
                    }
                    R.id.action_users ->{
                        findNavController().navigate(R.id.action_mainFragment_to_usersFragment)
                        true
                    }
                    else -> false
                }
            }

            imgSend.setOnClickListener {
                sendMessage()
            }

        }

        val messageListMap = sortedMapOf<String , Message>()

        val messageListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue<Message>()?.let { message ->
                    val id = snapshot.key.toString()
                    messageListMap[id] = message
                    val messageList = ArrayList(messageListMap.values)

                    var messageText = ""
                    messageList.forEach {
                        messageText+="Sender:${it.name}\n Message: ${it.text}\n"
                    }
                    viewBinding.txtMessage.text = messageText
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue<Message>()?.let {message ->
                    val id = snapshot.key.toString()
                    messageListMap[id] = message
                    val messageList = ArrayList(messageListMap.values)
                    var messageText = ""
                    messageList.forEach {
                        messageText+="Sender:${it.name}\n Message: ${it.text}\n"
                    }
                    viewBinding.txtMessage.text = messageText
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        Firebase.database.getReference("group_chat")
            .addChildEventListener(messageListener)
    }

    private fun sendMessage(){
        viewBinding.apply {
            val text = edtMessage.text.toString()

            if(text.isEmpty()){
                Toast.makeText(requireContext(), "please write something", Toast.LENGTH_SHORT).show()
                return
            }
            edtMessage.text.clear()
            val message = Message(text = text , name = "john")
            Firebase.database
                .getReference("group_chat")
                .push()
                .setValue(message)


        }
    }
}