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
import com.google.firebase.ktx.Firebase
import com.naulian.groupchat.databinding.FragmentFirstBinding

class FirstFragment : Fragment(R.layout.fragment_first) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewBinding = FragmentFirstBinding.bind(view)

        if(Firebase.auth.currentUser != null){
            findNavController().navigate(R.id.action_firstFragment_to_mainFragment)
        }


        val email = viewBinding.editTextEmail
        val password = viewBinding.editTextPassword
        val button = viewBinding.buttonSignin

        button.setOnClickListener {
            val emailString = email.text.toString()
            val passwordString = password.text.toString()

            if (emailString.isEmpty()) {
                email.setError("empty email")
                return@setOnClickListener
            }

            if (passwordString.isEmpty()) {
                password.setError("empty password")
                return@setOnClickListener
            }

            Firebase.auth.createUserWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(requireContext(), "successful", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_firstFragment_to_mainFragment)
                    } else {
                        println(it.exception?.message)
                        Toast.makeText(requireContext(), "failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}