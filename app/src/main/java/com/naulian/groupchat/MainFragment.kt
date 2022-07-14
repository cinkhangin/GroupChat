package com.naulian.groupchat

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.naulian.groupchat.databinding.FragmentMainBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainFragment : Fragment(R.layout.fragment_main) {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var viewBinding: FragmentMainBinding

    private var user = User()

   private val viewModel : MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentMainBinding.bind(view)

        initialize()
        loadUi()

        lifecycleScope.launch {
            viewModel.messageFlow.onEach {
                messageAdapter.submitList(it)
            }.launchIn(this)

            viewModel.userFlow.onEach {
                user = it
            }.launchIn(this)
        }
    }

    private fun initialize(){
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

        viewModel.getMessage()
        viewModel.getUser()
    }

    private fun loadUi(){
        viewBinding.apply {
            topAppBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_logout -> signOut()
                    R.id.action_users -> navigateUsers()
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

    private fun signOut() : Boolean{
        Firebase.auth.signOut()
        Toast.makeText(requireContext(), "sign out", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_mainFragment_to_firstFragment)
        return true
    }

    private fun navigateUsers() : Boolean{
        findNavController().navigate(R.id.action_mainFragment_to_usersFragment)
        return true
    }
}