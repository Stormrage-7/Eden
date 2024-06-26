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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eden.database.AppRepository
import com.example.eden.Eden
import com.example.eden.ui.viewmodels.HomeViewModel
import com.example.eden.ui.viewmodels.HomeViewModelFactory
import com.example.eden.adapters.PostAdapter
import com.example.eden.R
import com.example.eden.databinding.FragmentHomeBinding
import com.example.eden.entities.Community
import com.example.eden.models.CommunityModel
import com.example.eden.models.PostModel
import com.example.eden.util.PostUriGenerator
import com.google.android.material.divider.MaterialDividerItemDecoration

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

            override fun onCommunityClick(community: CommunityModel) {
                Intent(requireActivity(), CommunityDetailedActivity:: class.java).apply {
                    putExtra("CommunityObject", community)
                    startActivity(this)
                }
            }

            override fun onPostClick(post: PostModel) {
                openPost(post)
            }

            override fun onUserClick(userId: Int) {
                openProfile(userId)
            }

            override fun onUpvoteBtnClick(post: PostModel) {
                viewModel.upvotePost(post)
            }

            override fun onDownvoteBtnClick(post: PostModel) {
                viewModel.downvotePost(post)
            }

            override fun onBookmarkClick(post: PostModel) {
                viewModel.bookmarkPost(post)
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

            override fun scrollToTop() {
                fragmentHomeBinding.rvPosts.smoothScrollToPosition(0)
            }
        })

        fragmentHomeBinding.rvPosts.setItemViewCacheSize(10)
        fragmentHomeBinding.rvPosts.setHasFixedSize(true)
        fragmentHomeBinding.rvPosts.adapter = adapter
        fragmentHomeBinding.rvPosts.layoutManager = LinearLayoutManager(context)
        fragmentHomeBinding.rvPosts.addItemDecoration(
            MaterialDividerItemDecoration(
                requireContext(),
                LinearLayoutManager(requireContext()).orientation
            )
        )

        /******** TESTING ***********/
        viewModel.postCommunityCrossRefList.observe(requireActivity()) {
            if (it!=null){
                Log.i("PostCommunityCrossRef", it.toString())
            } else Log.i("PostCommunityCrossRef", "null")
        }

        viewModel.communityList.observe(requireActivity()) {
            it?.let{ adapter.updateCommunityList(it) }
        }

        viewModel.userList.observe(requireActivity()){
            it?.let { adapter.updateUserList(it) }
        }

        viewModel.postList.observe(requireActivity()) {
            it?.let {
                if (it.isEmpty()) {
                    fragmentHomeBinding.rvPosts.visibility = View.GONE
                    fragmentHomeBinding.tempImgView.visibility = View.VISIBLE
                    fragmentHomeBinding.tempTextView.visibility = View.VISIBLE
                } else {
                    fragmentHomeBinding.rvPosts.visibility = View.VISIBLE
                    fragmentHomeBinding.tempImgView.visibility = View.GONE
                    fragmentHomeBinding.tempTextView.visibility = View.GONE
                    Log.i("Inside PostList Observer", it.toString())
                }
                adapter.updatePostList(it)
            }
        }

        return fragmentHomeBinding.root
    }

    private fun openPost(post: PostModel){
        Intent(requireActivity(), PostDetailedActivity::class.java).apply {
            putExtra("PostObject", post)
            startActivity(this)
        }
    }

    private fun openProfile(userId: Int){
        Intent(requireActivity(), UserProfileActivity::class.java).apply {
            putExtra("UserId", userId)
            startActivity(this)
        }
    }

}