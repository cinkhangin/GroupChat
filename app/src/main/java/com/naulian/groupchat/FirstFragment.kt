package com.naulian.groupchat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.naulian.groupchat.databinding.FragmentFirstBinding

class FirstFragment : Fragment(R.layout.fragment_first) {
  private var viewBinding : FragmentFirstBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentFirstBinding.bind(view)
    }
}