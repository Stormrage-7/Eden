package com.example.eden.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.eden.databinding.FragmentInboxBinding

class InboxFragment: Fragment() {

    private lateinit var fragmentInboxBinding: FragmentInboxBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentInboxBinding = FragmentInboxBinding.inflate(layoutInflater)
        return fragmentInboxBinding.root
    }
}