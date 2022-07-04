package com.naulian.groupchat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.naulian.groupchat.databinding.FragmentFirstBinding

class FirstFragment : Fragment(R.layout.fragment_first) {
    private lateinit var viewBinding: FragmentFirstBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentFirstBinding.bind(view)

        if (Firebase.auth.currentUser != null) {
            navigate()
        }

        viewBinding.apply {
            buttonSignin.setOnClickListener {
                val emailString = editTextEmail.text.toString()
                val passwordString = editTextPassword.text.toString()
                val nameString = editTextName.text.toString()

                if (emailString.isEmpty()) {
                    editTextEmail.setError("empty email")
                    return@setOnClickListener
                }

                if (nameString.isEmpty()) {
                    editTextName.setError("empty name")
                    return@setOnClickListener
                }

                if (passwordString.isEmpty()) {
                    editTextPassword.setError("empty password")
                    return@setOnClickListener
                }

                signInAccount(emailString, passwordString)
            }
        }
    }

    private fun createAccount(email: String, pw: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, pw)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(requireContext(), "successful", Toast.LENGTH_SHORT).show()
                    val name = viewBinding.editTextName.text.toString()
                    val id = Firebase.auth.currentUser!!.uid
                    Firebase.database
                        .getReference("user")
                        .child(id)
                        .child("name")
                        .setValue(name)
                    navigate()
                } else {
                    println(it.exception?.message)
                    Toast.makeText(requireContext(), "failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signInAccount(email: String, pw: String) {
        Firebase.auth.signInWithEmailAndPassword(email, pw)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(requireContext(), "successful", Toast.LENGTH_SHORT).show()
                    navigate()
                } else createAccount(email, pw)
            }

    }

    private fun navigate() {
        findNavController().navigate(R.id.action_firstFragment_to_mainFragment)
        findNavController().graph.setStartDestination(R.id.mainFragment)
    }
}