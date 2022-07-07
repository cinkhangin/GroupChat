package com.naulian.groupchat

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.naulian.groupchat.databinding.UserLayoutBinding

class UserAdapter : ListAdapter<User, UserAdapter.UserViewholder>(UserDiffUtil()) {
    private var clickListener : ClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewholder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val userBinding = UserLayoutBinding.inflate(layoutInflater)
        return UserViewholder(userBinding)
    }

    override fun onBindViewHolder(holder: UserViewholder, position: Int) {
        holder.bind(position)
    }

    inner class UserViewholder(val userBinding: UserLayoutBinding) :
        RecyclerView.ViewHolder(userBinding.root) {

        fun bind(position: Int) {
            val user : User = getItem(position)

             userBinding.apply {
                 textName.text = user.name
                 textId.text = user.userId

                 root.setOnClickListener {
                   clickListener?.onClick(user)
                 }
             }
        }
    }

    fun setOnClickListener(listener: ClickListener){
        clickListener = listener
    }

    class UserDiffUtil : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.userId == newItem.userId
        }
    }
}