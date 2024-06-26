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
import com.example.eden.adapters.CommentAdapter
import com.example.eden.databinding.FragmentCommentSearchBinding
import com.example.eden.databinding.FragmentUserCommentsBinding
import com.example.eden.models.CommentModel
import com.example.eden.ui.viewmodels.ProfileViewModel

class UserProfileCommentsFragment: Fragment() {
    private lateinit var binding: FragmentUserCommentsBinding
    private lateinit var viewModel: ProfileViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_user_comments, container, false
        )
        viewModel = (requireActivity() as UserProfileActivity).viewModel
        binding.lifecycleOwner = this

        val adapter = CommentAdapter(requireActivity() as UserProfileActivity, object: CommentAdapter.CommentClickListener {
            override fun onCommentClick(comment: CommentModel) {
                Intent(activity as UserProfileActivity, PostDetailedActivity:: class.java).apply {
                    putExtra("CommentObject", comment)
                    startActivity(this)
                }
            }

            override fun onUpvoteClick(comment: CommentModel) {
                viewModel.upvoteComment(comment)
            }

            override fun onDownvoteClick(comment: CommentModel) {
                viewModel.downvoteComment(comment)
            }

            override fun onUserClick(userId: Int) {
                openProfile(userId)
            }
        })

        binding.rvComments.adapter = adapter
        binding.rvComments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvComments.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager(requireContext()).orientation
            )
        )

        viewModel.commentList.observe(activity as UserProfileActivity) {
            if (it != null) {
                if (it.isEmpty()) {
                    binding.rvComments.visibility = View.GONE
                    binding.scrollView.visibility = View.VISIBLE
//                    binding.tempImgView.visibility = View.VISIBLE
//                    binding.tempTextView.visibility = View.VISIBLE
                } else {
                    binding.rvComments.visibility = View.VISIBLE
                    binding.scrollView.visibility = View.GONE
//                    binding.tempImgView.visibility = View.GONE
//                    binding.tempTextView.visibility = View.GONE
                }
                adapter.updateCommentList(it)
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