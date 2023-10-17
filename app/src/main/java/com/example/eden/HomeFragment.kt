package com.example.eden

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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

        val application = requireNotNull(this.activity).application
        val dataSource = AppDatabase.getDatabase(application).postDao()
        val viewModelFactory = HomeViewModelFactory(dataSource, application)

        Log.i("HomeFragment", AppDatabase.getDatabase(application).isOpen.toString())
        viewModel = ViewModelProvider(this.requireActivity(), viewModelFactory).get(HomeViewModel::class.java)
        Log.i("HomeFragment", viewModel.toString())

        fragmentHomeBinding.lifecycleOwner = this
        fragmentHomeBinding.homeViewModel = viewModel



        val adapter = context?.let { PostAdapter(viewModel.postList, context = it) }
        fragmentHomeBinding.rvPosts.adapter = adapter
        fragmentHomeBinding.rvPosts.layoutManager = LinearLayoutManager(context)
        fragmentHomeBinding.rvPosts.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager(context).orientation
            )
        )

        viewModel.postList.observe(this.requireActivity(), Observer {
            it.let {
                adapter!!.update(it)
                Log.i("Inside PostList Observer", it.toString())
                Log.i("Inside PostList Observer", adapter.postList.toString())
            }
        })


        return fragmentHomeBinding.root
    }




}