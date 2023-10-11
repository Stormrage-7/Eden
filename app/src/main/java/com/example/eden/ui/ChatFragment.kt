package com.example.eden.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.eden.databinding.FragmentChatBinding

class ChatFragment: Fragment() {
    private lateinit var fragmentChatBinding: FragmentChatBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentChatBinding = FragmentChatBinding.inflate(layoutInflater)
        return fragmentChatBinding.root
    }
}