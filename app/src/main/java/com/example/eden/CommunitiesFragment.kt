package com.example.eden

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
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
//        fragmentCommunitiesBinding = FragmentCommunitiesBinding.inflate(layoutInflater)
        fragmentCommunitiesBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_communities, container, false)
        viewModel = ViewModelProvider(this).get(CommunitiesViewModel::class.java)

        fragmentCommunitiesBinding.communitiesViewModel = viewModel
        fragmentCommunitiesBinding.lifecycleOwner = this //Very Important line of code that allowed LiveData to update the layout

        // Commented out because of Data Binding with which Direct connection between layout file and data in ViewModel has been created

//        fragmentCommunitiesBinding.textViewCount.text = viewModel.count.toString()

//        viewModel.count.observe(viewLifecycleOwner, Observer {updated ->
//            fragmentCommunitiesBinding.textViewCount.text = updatedCount.toString()
//        })

//        fragmentCommunitiesBinding.btnIncrease.setOnClickListener {
//            viewModel.increaseCount()
//            updateCountText()
//        }

        return fragmentCommunitiesBinding.root
    }

//    private fun updateCountText(){
//        fragmentCommunitiesBinding.textViewCount.text = viewModel.count.toString()
//    }
}