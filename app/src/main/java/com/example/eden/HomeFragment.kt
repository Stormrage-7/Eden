package com.example.eden

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eden.databinding.FragmentHomeBinding

class HomeFragment: Fragment(){
    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Inflation
        fragmentHomeBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )
        val view = fragmentHomeBinding.root

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)


        val adapter = context?.let { PostAdapter(viewModel.postList, it) }
        fragmentHomeBinding.rvPosts.adapter = adapter
        fragmentHomeBinding.rvPosts.layoutManager = LinearLayoutManager(context)
        fragmentHomeBinding.rvPosts.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager(context).orientation
            )
        )
        return view
    }
}