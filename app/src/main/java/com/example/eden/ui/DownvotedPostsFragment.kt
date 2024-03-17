package com.example.eden.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eden.R
import com.example.eden.adapters.PostAdapter
import com.example.eden.databinding.FragmentPostSearchBinding
import com.example.eden.models.CommunityModel
import com.example.eden.models.PostModel
import com.example.eden.ui.viewmodels.PostInteractionsViewModel
import com.example.eden.util.PostUriGenerator

class DownvotedPostsFragment: Fragment() {
    private lateinit var fragmentPostSearchBinding: FragmentPostSearchBinding
    private lateinit var viewModel: PostInteractionsViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentPostSearchBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_post_search, container, false
        )
        viewModel = (activity as PostInteractionsActivity).viewModel

        fragmentPostSearchBinding.lifecycleOwner = this

        val adapter = PostAdapter(activity as PostInteractionsActivity, object : PostAdapter.PostListener {

            override fun onCommunityClick(community: CommunityModel) {
                Intent(requireActivity() as SearchableActivity, CommunityDetailedActivity::class.java).apply {
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

            override fun onUpvoteBtnClick(post: PostModel) {}
            override fun onDownvoteBtnClick(post: PostModel) {}

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
                fragmentPostSearchBinding.rvPosts.smoothScrollToPosition(0)
            }
        })

        fragmentPostSearchBinding.rvPosts.adapter = adapter
        fragmentPostSearchBinding.rvPosts.layoutManager = LinearLayoutManager(context)
        fragmentPostSearchBinding.rvPosts.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager(context).orientation
            )
        )

        viewModel.communityList.observe(requireActivity()) {
            it?.let { adapter.updateCommunityList(it) }
        }

        viewModel.userList.observe(requireActivity()){
            it?.let { adapter.updateUserList(it) }
        }

        viewModel.downvotedPostList.observe(requireActivity()) {
            it?.let {
                if (it.isEmpty()) {
                    fragmentPostSearchBinding.rvPosts.visibility = View.GONE
                    fragmentPostSearchBinding.tempImgView.visibility = View.VISIBLE
                    fragmentPostSearchBinding.tempTextView.visibility = View.VISIBLE
                } else {
                    fragmentPostSearchBinding.rvPosts.visibility = View.VISIBLE
                    fragmentPostSearchBinding.tempImgView.visibility = View.GONE
                    fragmentPostSearchBinding.tempTextView.visibility = View.GONE
                }
                adapter.updatePostList(it)
            }
        }

        return fragmentPostSearchBinding.root
    }

    private fun openProfile(userId: Int){
        Intent(requireActivity(), UserProfileActivity::class.java).apply {
            putExtra("UserId", userId)
            startActivity(this)
        }
    }
}