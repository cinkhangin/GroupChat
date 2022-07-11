package com.naulian.groupchat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.naulian.groupchat.databinding.FragmentUsersBinding


class UsersFragment : Fragment(R.layout.fragment_users) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewBinding = FragmentUsersBinding.bind(view)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        val userAdapter = UserAdapter()

        val userListMap = hashMapOf<String , User>()

        val userListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue<User>()?.let { user ->
                    val id = snapshot.key.toString()
                    user.userId = id
                    userListMap[id] = user
                    val userList = ArrayList(userListMap.values)
                    userAdapter.submitList(userList)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue<User>()?.let { user ->
                    val id = snapshot.key.toString()
                    user.userId = id
                    userListMap[id] = user
                    val userList = ArrayList(userListMap.values)
                    userAdapter.submitList(userList)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        Firebase.database
            .getReference("user")
            .addChildEventListener(userListener)


        val clickListener = object : ClickListener {
            override fun onClick(user : User) {
               viewBinding.apply {
                   edtUserName.visibility = View.VISIBLE
                   btnSave.visibility = View.VISIBLE

                   edtUserName.setText(user.name)
                   btnSave.setOnClickListener {
                       val newName = edtUserName.text.toString()
                       Firebase.database
                           .getReference("user")
                           .child(user.userId)
                           .child("name")
                           .setValue(newName).addOnCompleteListener {
                               if(it.isSuccessful){
                                   edtUserName.visibility = View.GONE
                                   btnSave.visibility = View.GONE
                               }
                           }
                   }
               }
            }
        }

        userAdapter.setOnClickListener(clickListener)


        viewBinding.apply {
            userList.layoutManager = linearLayoutManager
            userList.adapter = userAdapter
        }
    }
}