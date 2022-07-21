package com.naulian.groupchat

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.naulian.groupchat.databinding.LayoutMessageBinding

class MessageAdapter : ListAdapter<Message, MessageAdapter.UserViewholder>(MessageDiffUtil()) {
    private var clickListener: ClickListener? = null
    private var longClickListener: LongClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewholder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val messageBinding = LayoutMessageBinding.inflate(layoutInflater, parent , false)
        return UserViewholder(messageBinding)
    }

    override fun onBindViewHolder(holder: UserViewholder, position: Int) {
        holder.bind(position)
    }

    inner class UserViewholder(val messageBinding: LayoutMessageBinding) :
        RecyclerView.ViewHolder(messageBinding.root) {

        private val context = messageBinding.root.context
        private val id = Firebase.auth.currentUser?.uid ?: ""
        private val myColorRes = R.color.myMessageColor
        private val otherColorRes = R.color.otherMessageColor

        private val myColor = ContextCompat.getColor(context , myColorRes)
        private val otherColor = ContextCompat.getColor(context , otherColorRes)

        fun bind(position: Int) {
            val message: Message = getItem(position)

            messageBinding.apply {
                textName.text = message.name
                textMessage.text = message.text

                if (message.id == id) {
                    card.setCardBackgroundColor(myColor)
                    root.gravity = Gravity.END
                } else {
                    card.setCardBackgroundColor(otherColor)
                    root.gravity = Gravity.START
                }

                if(message.seen){
                    imgSeen.setImageResource(R.drawable.ic_baseline_check_circle_24)
                }else{
                    imgSeen.setImageResource(R.drawable.ic_baseline_check_circle_outline_24)
                }

                if(position > 0){
                    val preMessage = getItem(position - 1)
                    textName.isVisible = preMessage.id != message.id

                }

                if(position < itemCount - 1){
                    val nextMessage = getItem(position+1)
                    imgSeen.isVisible = nextMessage.id != message.id
                }


                card.setOnLongClickListener {
                    longClickListener?.onLongClick(message)
                    true
                }
            }
        }
    }

    fun setOnClickListener(listener: ClickListener) {
        clickListener = listener
    }

    fun setOnLongClickListener(longClickListener: LongClickListener){
        this.longClickListener = longClickListener
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