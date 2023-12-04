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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eden.databinding.FragmentHomeBinding
import com.example.eden.entities.Community
import com.example.eden.entities.Post

class HomeFragment: Fragment(){
    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
//    private lateinit var database: AppDatabase
    private lateinit var repository: AppRepository
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

        val application = requireActivity().application as Eden
        repository = application.repository
        factory = HomeViewModelFactory(repository, application)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]


        viewModel = ViewModelProvider(this.requireActivity(), factory).get(HomeViewModel::class.java)

        fragmentHomeBinding.lifecycleOwner = this   // Important
        fragmentHomeBinding.homeViewModel = viewModel


        val adapter = context?.let {
            PostAdapter(context = it, object : PostAdapter.PostListener{
                // ANONYMOUS CLASS IMPLEMENTATION OF POSTCLICKLISTENER INTERFACE
            override fun getCommunityIdFromPostId(position: Int): Int {
                return 1
            }

            override fun onPostClick(post: Post) {
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
        viewModel.joinedCommunitiesList.observe(this.requireActivity(), Observer {
            Log.i("HomeFragment", "${it.toString()}")
            adapter!!.updateJoinedCommunityList(it)
        })

        viewModel.postCommunityCrossRefList.observe(this.requireActivity(), Observer {
            Log.i("HomeFragment", "${it.toString()}")
            adapter!!.updatePostCommunityCrossRefList(it)
        })

        viewModel.communityList.observe(this.requireActivity(), Observer {
            Log.i("HomeFragment", "${it.toString()}")
            adapter!!.updateCommunityList(it)
//            Log.i("HomeFragment", "${viewModel.localCommunityList}")
        })

        viewModel.postList.observe(this.requireActivity(), Observer {
            it.let {
                if(it.isEmpty()){
                    fragmentHomeBinding.rvPosts.visibility = View.GONE
                    fragmentHomeBinding.tempImgView.visibility = View.VISIBLE
                    fragmentHomeBinding.tempTextView.visibility = View.VISIBLE
                    adapter!!.updatePostList(it)
                }
                else {
                    fragmentHomeBinding.rvPosts.visibility = View.VISIBLE
                    fragmentHomeBinding.tempImgView.visibility = View.GONE
                    fragmentHomeBinding.tempTextView.visibility = View.GONE
//                    it.filter { post -> adapter!!.joinedCommunitiesList.contains(post.communityId) }
                    adapter!!.updatePostList(it)
                    Log.i("Inside PostList Observer", it.toString())
                    Log.i("Inside PostList Observer", adapter.postList.toString())
                }
            }
        })

        return fragmentHomeBinding.root
    }




}