package com.example.eden.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eden.database.AppRepository
import com.example.eden.Eden
import com.example.eden.viewmodels.HomeViewModel
import com.example.eden.viewmodels.HomeViewModelFactory
import com.example.eden.adapters.PostAdapter
import com.example.eden.R
import com.example.eden.databinding.FragmentHomeBinding
import com.example.eden.entities.Community
import com.example.eden.entities.Post
import timber.log.Timber

class HomeFragment: Fragment(){
    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var repository: AppRepository
    private lateinit var factory: HomeViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //Inflation
        fragmentHomeBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )

        val application = requireActivity()
        repository = (application.application as Eden).repository
        requireActivity().application
        factory = HomeViewModelFactory(repository, application)
        viewModel = ViewModelProvider(this.requireActivity(), factory)[HomeViewModel::class.java]

        fragmentHomeBinding.lifecycleOwner = viewLifecycleOwner   // Important
        fragmentHomeBinding.homeViewModel = viewModel


        val adapter = PostAdapter(context = requireContext(), object : PostAdapter.PostListener {
            // ANONYMOUS CLASS IMPLEMENTATION OF POSTCLICKLISTENER INTERFACE
        override fun getCommunityIdFromPostId(position: Int): Int {
            return 1
        }

        override fun onPostClick(post: Post, community: Community) {
            Intent(requireActivity(), PostDetailedActivity::class.java).apply {
                putExtra("PostObject", post)
                putExtra("CommunityObject", community)
                startActivity(this)
            }
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

    })
        fragmentHomeBinding.rvPosts.adapter = adapter
        fragmentHomeBinding.rvPosts.layoutManager = LinearLayoutManager(context)
        fragmentHomeBinding.rvPosts.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager(context).orientation
            )
        )

        viewModel.communityList.observe(this.requireActivity(), Observer {
            Timber.i(it.toString())
            adapter.updateCommunityList(it)
        })

        viewModel.postList.observe(this.requireActivity(), Observer {
            it.let {
                if(it.isEmpty()){
                    fragmentHomeBinding.rvPosts.visibility = View.GONE
                    fragmentHomeBinding.tempImgView.visibility = View.VISIBLE
                    fragmentHomeBinding.tempTextView.visibility = View.VISIBLE
                    adapter.updatePostList(it)
                }
                else {
                    fragmentHomeBinding.rvPosts.visibility = View.VISIBLE
                    fragmentHomeBinding.tempImgView.visibility = View.GONE
                    fragmentHomeBinding.tempTextView.visibility = View.GONE
                    adapter.updatePostList(it)
                    Log.i("Inside PostList Observer", it.toString())
                    Log.i("Inside PostList Observer", adapter.postList.toString())
                }
            }
        })

        return fragmentHomeBinding.root
    }




}