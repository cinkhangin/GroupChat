package com.naulian.groupchat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.naulian.groupchat.databinding.FragmentMainBinding

class MainFragment : Fragment(R.layout.fragment_main) {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var viewBinding: FragmentMainBinding

    private var user = User()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentMainBinding.bind(view)

        messageAdapter = MessageAdapter()

        linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        val dataObserver = object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                scrollMessage()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                super.onItemRangeChanged(positionStart, itemCount)
                scrollMessage()
            }
        }

        messageAdapter.registerAdapterDataObserver(dataObserver)

        viewBinding.apply {
            topAppBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_logout -> {
                        Firebase.auth.signOut()
                        Toast.makeText(requireContext(), "sign out", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_mainFragment_to_firstFragment)
                        true
                    }
                    R.id.action_users -> {
                        findNavController().navigate(R.id.action_mainFragment_to_usersFragment)
                        true
                    }
                    else -> false
                }
            }

            imgSend.setOnClickListener {
                sendMessage()
            }

            listMessage.apply {
                adapter = messageAdapter
                layoutManager = linearLayoutManager
            }

        }

        val messageListMap = sortedMapOf<String, Message>()

        val messageListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue<Message>()?.let { message ->
                    val id = snapshot.key.toString()
                    messageListMap[id] = message
                    message.messageId = id
                    val messageList = ArrayList(messageListMap.values)
                    messageAdapter.submitList(messageList)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue<Message>()?.let { message ->
                    val id = snapshot.key.toString()
                    messageListMap[id] = message
                    message.messageId = id
                    val messageList = ArrayList(messageListMap.values)
                    messageAdapter.submitList(messageList)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                snapshot.getValue<Message>()?.let { message ->
                    val id = snapshot.key.toString()
                    messageListMap.remove(id)
                    val messageList = ArrayList(messageListMap.values)
                    messageAdapter.submitList(messageList)
                }
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        }

        Firebase.database.getReference("group_chat")
            .addChildEventListener(messageListener)

        val userListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue<User>()?.let {
                    val id = snapshot.key.toString()
                    it.userId = id
                    user = it
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        }


        val id = Firebase.auth.currentUser?.uid ?: ""
        Firebase.database.getReference("user")
            .child(id)
            .addValueEventListener(userListener)

        val longClickListener = object : LongClickListener{
            override fun onLongClick(message: Message) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Message")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Delete"){ dialog, which ->
                        Firebase.database.getReference("group_chat")
                            .child(message.messageId)
                            .removeValue()
                    }
                    .setNegativeButton("Cancel"){a ,b ->}
                    .show()
            }
        }

        messageAdapter.setOnLongClickListener(longClickListener)

    }

    private fun scrollMessage() {
        linearLayoutManager.smoothScrollToPosition(viewBinding.listMessage , null , messageAdapter.itemCount)
    }

    private fun sendMessage() {
        viewBinding.apply {
            val text = edtMessage.text.toString()

            if (text.isEmpty()) {
                Toast.makeText(requireContext(), "please write something", Toast.LENGTH_SHORT)
                    .show()
                return
            }

            edtMessage.text.clear()
            val message = Message(text = text, name = user.name)
            Firebase.database
                .getReference("group_chat")
                .push()
                .setValue(message)
            scrollMessage()
        }
    }
}