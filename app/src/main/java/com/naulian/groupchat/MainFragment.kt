package com.naulian.groupchat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.naulian.groupchat.databinding.FragmentMainBinding

class MainFragment : Fragment(R.layout.fragment_main) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewBinding = FragmentMainBinding.bind(view)

        viewBinding.apply {
            topAppBar.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.action_logout -> {
                        Firebase.auth.signOut()
                        Toast.makeText(requireContext(), "sign out", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_mainFragment_to_firstFragment)
                        true
                    }
                    else -> false
                }
            }

            var counter = 0
            btnMain.apply {
                setOnClickListener {
                   findNavController().navigate(R.id.action_mainFragment_to_usersFragment)
                }

               text = "text"
            }

        }
    }

    private fun changeData(data : String){
        val ref = Firebase.database.getReference("groupChat")
        ref.setValue(data)
    }
}