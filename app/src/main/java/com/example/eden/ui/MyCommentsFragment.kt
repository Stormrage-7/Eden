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
import com.example.eden.models.CommentModel
import com.example.eden.ui.viewmodels.PostInteractionsViewModel

class MyCommentsFragment: Fragment() {
    private lateinit var binding: FragmentCommentSearchBinding
    private lateinit var viewModel: PostInteractionsViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_comment_search, container, false
        )
        viewModel = (activity as PostInteractionsActivity).viewModel
        binding.lifecycleOwner = this

        val adapter = CommentAdapter(activity as PostInteractionsActivity, object: CommentAdapter.CommentClickListener {
            override fun onCommentClick(comment: CommentModel) {
                Intent(activity as PostInteractionsActivity, PostDetailedActivity:: class.java).apply {
                    putExtra("CommentObject", comment)
                    startActivity(this)
                }
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

        viewModel.userList.observe(requireActivity()) {
            it?.let {
                adapter.updateUserList(it)
            }
        }

        viewModel.commentList.observe(activity as PostInteractionsActivity) {
            if (it != null) {
                if (it.isEmpty()) {
                    binding.rvComments.visibility = View.GONE
                    binding.tempImgView.visibility = View.VISIBLE
                    binding.tempTextView.visibility = View.VISIBLE
                } else {
                    binding.rvComments.visibility = View.VISIBLE
                    binding.tempImgView.visibility = View.GONE
                    binding.tempTextView.visibility = View.GONE
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