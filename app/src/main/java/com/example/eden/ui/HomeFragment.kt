package com.example.eden.ui

import android.annotation.SuppressLint
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
import com.example.eden.ui.viewmodels.HomeViewModel
import com.example.eden.ui.viewmodels.HomeViewModelFactory
import com.example.eden.adapters.PostAdapter
import com.example.eden.R
import com.example.eden.databinding.FragmentHomeBinding
import com.example.eden.entities.Community
import com.example.eden.entities.Post
import com.example.eden.util.PostUriGenerator
import com.google.android.material.divider.MaterialDividerItemDecoration
import timber.log.Timber

class HomeFragment: Fragment(){
    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var repository: AppRepository
    private lateinit var factory: HomeViewModelFactory

    @SuppressLint("LogNotTimber")
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
        repository = (requireActivity().application as Eden).repository
        factory = HomeViewModelFactory(repository, application)
        viewModel = ViewModelProvider(this.requireActivity(), factory)[HomeViewModel::class.java]

        fragmentHomeBinding.lifecycleOwner = viewLifecycleOwner   // Important
        fragmentHomeBinding.homeViewModel = viewModel


        val adapter = PostAdapter(context = requireContext(), object : PostAdapter.PostListener {

            override fun onCommunityClick(community: Community) {
                Intent(requireActivity(), CommunityDetailedActivity:: class.java).apply {
                    putExtra("CommunityObject", community)
                    startActivity(this)
                }
            }

            override fun onPostClick(post: Post) {
                Intent(requireActivity(), PostDetailedActivity::class.java).apply {
                    putExtra("PostObject", post)
                    startActivity(this)
                }
            }

            override fun onUpvoteBtnClick(post: Post) {
                viewModel.upvotePost(post)
            }

            override fun onDownvoteBtnClick(post: Post) {
                viewModel.downvotePost(post)
            }

            override fun onShareBtnClick(postId: Int, communityId: Int) {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, PostUriGenerator.generate(postId, communityId))
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
        })

        fragmentHomeBinding.rvPosts.setItemViewCacheSize(10)
        fragmentHomeBinding.rvPosts.setHasFixedSize(true)
        fragmentHomeBinding.rvPosts.adapter = adapter
        fragmentHomeBinding.rvPosts.layoutManager = LinearLayoutManager(context)
        fragmentHomeBinding.rvPosts.addItemDecoration(
            MaterialDividerItemDecoration(
                requireContext(),
                LinearLayoutManager(context).orientation
            )
        )

        /******** TESTING ***********/
        viewModel.postCommunityCrossRefList.observe(this.requireActivity()) {
            if (it!=null){
                Log.i("PostCommunityCrossRef", it.toString())
            } else Log.i("PostCommunityCrossRef", "null")
        }

        viewModel.joinedCommunitiesList.observe(this.requireActivity()) {
            adapter.joinedCommunitiesList = it
        }
        viewModel.communityList.observe(this.requireActivity()) {
            adapter.updateCommunityList(it)
        }

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