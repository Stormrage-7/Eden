package com.example.eden

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.eden.databinding.FragmentCommunitiesBinding

class CommunitiesFragment: Fragment() {
    private lateinit var fragmentCommunitiesBinding: FragmentCommunitiesBinding
    private lateinit var viewModel: CommunitiesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentCommunitiesBinding = FragmentCommunitiesBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(CommunitiesViewModel::class.java)
        fragmentCommunitiesBinding.textViewCount.text = viewModel.count.toString()

        viewModel.count.observe(viewLifecycleOwner, Observer {updatedCount ->
            fragmentCommunitiesBinding.textViewCount.text = updatedCount.toString()
        })

        fragmentCommunitiesBinding.btnIncrease.setOnClickListener {
            viewModel.increaseCount()
//            updateCountText()
        }

        return fragmentCommunitiesBinding.root
    }

//    private fun updateCountText(){
//        fragmentCommunitiesBinding.textViewCount.text = viewModel.count.toString()
//    }
}