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
import com.example.eden.databinding.FragmentUserPostsBinding
import com.example.eden.models.CommunityModel
import com.example.eden.models.PostModel
import com.example.eden.ui.viewmodels.PostInteractionsViewModel
import com.example.eden.ui.viewmodels.ProfileViewModel
import com.example.eden.util.PostUriGenerator

class UserProfilePostsFragment: Fragment() {
    private lateinit var binding: FragmentUserPostsBinding
    private lateinit var viewModel: ProfileViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_user_posts, container, false
        )
        viewModel = (activity as UserProfileActivity).viewModel

        binding.lifecycleOwner = this

        val adapter = PostAdapter(activity as UserProfileActivity, object : PostAdapter.PostListener {

            override fun onCommunityClick(community: CommunityModel) {
                Intent(requireActivity() as UserProfileActivity, CommunityDetailedActivity::class.java).apply {
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

            override fun onUpvoteBtnClick(post: PostModel) { viewModel.upvotePost(post) }
            override fun onDownvoteBtnClick(post: PostModel) { viewModel.downvotePost(post) }
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
                binding.rvPosts.smoothScrollToPosition(0)
            }
        })

        binding.rvPosts.adapter = adapter
        binding.rvPosts.layoutManager = LinearLayoutManager(context)
        binding.rvPosts.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager(context).orientation
            )
        )

        viewModel.communityList.observe(requireActivity()) {
            it?.let {
                adapter.updateCommunityList(it)
            }
        }

        viewModel.postList.observe(activity as UserProfileActivity) {
            it?.let {
                if(it.isEmpty()){
                    binding.rvPosts.visibility = View.GONE
                    binding.scrollView.visibility = View.VISIBLE
//                    binding.tempImgView.visibility = View.VISIBLE
//                    binding.tempTextView.visibility = View.VISIBLE
                }
                else {
                    binding.rvPosts.visibility = View.VISIBLE
                    binding.scrollView.visibility = View.GONE
//                    binding.tempImgView.visibility = View.GONE
//                    binding.tempTextView.visibility = View.GONE
                }
                adapter.updatePostList(it)
            }
        }

        return binding.root
    }

    private fun openProfile(userId: Int){
        Intent(requireActivity(), UserProfileActivity::class.java).apply {
            putExtra("UserId", userId)
            startActivity(this)
        }
    }
}