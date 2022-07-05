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

        val userArrayList = arrayListOf<User>()

        val userListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue<UserMap>()?.let {
                    val id = snapshot.key.toString()
                    val user = User(name = it.name, userId = id)
                    userArrayList.add(user)
                    userAdapter.submitList(userArrayList)
                    println(userArrayList)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

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


        viewBinding.apply {
            userList.layoutManager = linearLayoutManager
            userList.adapter = userAdapter
        }
    }
}