package com.example.eden

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
        viewModel = ViewModelProvider(this.requireActivity(), factory)[HomeViewModel::class.java]

        fragmentHomeBinding.lifecycleOwner = viewLifecycleOwner   // Important
        fragmentHomeBinding.homeViewModel = viewModel


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
        fragmentHomeBinding.rvPosts.adapter = adapter
        fragmentHomeBinding.rvPosts.layoutManager = LinearLayoutManager(context)
        fragmentHomeBinding.rvPosts.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager(context).orientation
            )
        )

        viewModel.communityList.observe(this.requireActivity(), Observer {
            Log.i("HomeFragment", "${it.toString()}")
            adapter!!.updateCommunityList(it)
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