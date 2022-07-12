package com.naulian.groupchat

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.naulian.groupchat.databinding.LayoutMessageBinding

class MessageAdapter : ListAdapter<Message, MessageAdapter.UserViewholder>(MessageDiffUtil()) {
    private var clickListener: ClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewholder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val messageBinding = LayoutMessageBinding.inflate(layoutInflater)
        return UserViewholder(messageBinding)
    }

    override fun onBindViewHolder(holder: UserViewholder, position: Int) {
        holder.bind(position)
    }

    inner class UserViewholder(val messageBinding: LayoutMessageBinding) :
        RecyclerView.ViewHolder(messageBinding.root) {

        private val id = Firebase.auth.currentUser?.uid ?: ""
        private val myColor = R.color.myMessageColor
        private val otherColor = R.color.otherMessageColor


        fun bind(position: Int) {
            val message: Message = getItem(position)

            messageBinding.apply {
                textName.text = message.name
                textMessage.text = message.text

                card.setCardBackgroundColor(myColor)
               /* if (message.id == id) {

                } else {
                    card.setCardBackgroundColor(otherColor)
                    linearLayout.gravity = Gravity.START
                }*/
            }
        }
    }

    fun setOnClickListener(listener: ClickListener) {
        clickListener = listener
    }

    class MessageDiffUtil : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.text == newItem.text
        }
    }
}