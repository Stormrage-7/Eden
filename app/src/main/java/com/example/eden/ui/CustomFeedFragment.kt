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
import com.example.eden.database.AppRepository
import com.example.eden.Eden
import com.example.eden.ui.viewmodels.HomeViewModel
import com.example.eden.ui.viewmodels.HomeViewModelFactory
import com.example.eden.adapters.PostAdapter
import com.example.eden.databinding.FragmentCustomFeedBinding
import com.example.eden.models.CommunityModel
import com.example.eden.models.PostModel
import com.example.eden.util.PostUriGenerator

class CustomFeedFragment: Fragment() {
    private lateinit var fragmentCustomFeedBinding: FragmentCustomFeedBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var repository: AppRepository
    private lateinit var factory: HomeViewModelFactory
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentCustomFeedBinding = FragmentCustomFeedBinding.inflate(layoutInflater)

        val application = requireActivity().application as Eden
        repository = application.repository
        factory = HomeViewModelFactory(repository, requireActivity())
        viewModel = ViewModelProvider(this.requireActivity(), factory)[HomeViewModel::class.java]

        fragmentCustomFeedBinding.lifecycleOwner = viewLifecycleOwner
        fragmentCustomFeedBinding.customFeedViewModel = viewModel


        val adapter = PostAdapter(context = requireContext(), object : PostAdapter.PostListener{

            override fun onCommunityClick(community: CommunityModel) {
                Intent(requireActivity() as HomeScreenActivity, CommunityDetailedActivity:: class.java).apply {
                    putExtra("CommunityObject", community)
                    startActivity(this)
                }
            }

            override fun onPostClick(post: PostModel) {
                Intent(requireActivity(), PostDetailedActivity::class.java).apply {
                    putExtra("PostObject", post)
                    startActivity(this)
                }
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
                fragmentCustomFeedBinding.rvPosts.smoothScrollToPosition(0)
            }
        })

        fragmentCustomFeedBinding.rvPosts.adapter = adapter
        fragmentCustomFeedBinding.rvPosts.layoutManager = LinearLayoutManager(context)
        fragmentCustomFeedBinding.rvPosts.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager(context).orientation
            )
        )

        viewModel.joinedCommunityList.observe(requireActivity()) {
            adapter.updateJoinedCommunityList(it)
        }

        viewModel.userList.observe(requireActivity()){
            it?.let { adapter.updateUserList(it) }
        }

        viewModel.joinedCommunityPostList.observe(requireActivity()) {
            it?.let {
                if (it.isEmpty()) {
                    fragmentCustomFeedBinding.rvPosts.visibility = View.GONE
                    fragmentCustomFeedBinding.tempImgView.visibility = View.VISIBLE
                    fragmentCustomFeedBinding.tempTextView.visibility = View.VISIBLE
                    Log.i("CustomFeedFragment", "Empty")
                } else {
                    fragmentCustomFeedBinding.rvPosts.visibility = View.VISIBLE
                    fragmentCustomFeedBinding.tempImgView.visibility = View.GONE
                    fragmentCustomFeedBinding.tempTextView.visibility = View.GONE
                }
                adapter.updatePostList(it)
            }
            Log.i("CustomFeedFragment", "PostList Observer")
        }


        return fragmentCustomFeedBinding.root
    }

    private fun openProfile(userId: Int){
        Intent(requireActivity(), UserProfileActivity::class.java).apply {
            putExtra("UserId", userId)
            startActivity(this)
        }
    }
}