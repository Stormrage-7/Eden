package com.example.eden

import android.app.Application
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
    private lateinit var database: AppDatabase
    private lateinit var repository: PostRepository
    private lateinit var factory: HomeViewModelFactory

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
        database = AppDatabase.getDatabase(application)
        repository = PostRepository(database)
        factory = HomeViewModelFactory(repository, application)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]


        viewModel = ViewModelProvider(this.requireActivity(), factory).get(HomeViewModel::class.java)
        Log.i("HomeFragment", viewModel.toString())

        fragmentHomeBinding.lifecycleOwner = this   // Important
        fragmentHomeBinding.homeViewModel = viewModel


        val adapter = context?.let {
            PostAdapter(context = it, object : PostAdapter.PostClickListener{
                // ANONYMOUS CLASS IMPLEMENTATION OF POSTCLICKLISTENER INTERFACE
            override fun onPostClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onUpvoteBtnClick(post: Post) {
                viewModel.upvotePost(post)
            }

            override fun onDownvoteBtnClick(post: Post) {
                viewModel.downvotePost(post)
            }

            override fun onPostLongClick(position: Int) {
                TODO("Not yet implemented")
            }

        }) }
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