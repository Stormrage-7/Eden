package com.example.eden.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eden.AppRepository
import com.example.eden.Eden
import com.example.eden.HomeViewModel
import com.example.eden.HomeViewModelFactory
import com.example.eden.PostAdapter
import com.example.eden.PostDetailedActivity
import com.example.eden.databinding.FragmentCustomFeedBinding
import com.example.eden.entities.Community
import com.example.eden.entities.Post

class CustomFeedFragment: Fragment() {
    private lateinit var fragmentCustomFeedBinding: FragmentCustomFeedBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var repository: AppRepository
    private lateinit var factory: HomeViewModelFactory
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentCustomFeedBinding = FragmentCustomFeedBinding.inflate(layoutInflater)

        val application = requireActivity().application as Eden
        repository = application.repository
        factory = HomeViewModelFactory(repository, application)
        viewModel = ViewModelProvider(this.requireActivity(), factory)[HomeViewModel::class.java]

        fragmentCustomFeedBinding.lifecycleOwner = this   // Important
        fragmentCustomFeedBinding.customFeedViewModel = viewModel


        val adapter = context?.let {
            PostAdapter(context = it, object : PostAdapter.PostListener{
                // ANONYMOUS CLASS IMPLEMENTATION OF POSTCLICKLISTENER INTERFACE
                override fun getCommunityIdFromPostId(position: Int): Int {
                    return 1
                }

                override fun onPostClick(post: Post, community: Community) {
//                Toast.makeText(requireActivity(), "${post.toString()}", Toast.LENGTH_SHORT).show()
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

            }) }
        fragmentCustomFeedBinding.rvPosts.adapter = adapter
        fragmentCustomFeedBinding.rvPosts.layoutManager = LinearLayoutManager(context)
        fragmentCustomFeedBinding.rvPosts.addItemDecoration(
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
                    fragmentCustomFeedBinding.rvPosts.visibility = View.GONE
                    fragmentCustomFeedBinding.tempImgView.visibility = View.VISIBLE
                    fragmentCustomFeedBinding.tempTextView.visibility = View.VISIBLE
                    adapter!!.updatePostList(it, "CustomFeed")
                }
                else {
                    fragmentCustomFeedBinding.rvPosts.visibility = View.VISIBLE
                    fragmentCustomFeedBinding.tempImgView.visibility = View.GONE
                    fragmentCustomFeedBinding.tempTextView.visibility = View.GONE
//                    it.filter { post -> adapter!!.joinedCommunitiesList.contains(post.communityId) }
                    adapter!!.updatePostList(it, "CustomFeed")
                    Log.i("Inside PostList Observer", it.toString())
                    Log.i("Inside PostList Observer", adapter.postList.toString())
                }
            }
        })

        return fragmentCustomFeedBinding.root
    }
}