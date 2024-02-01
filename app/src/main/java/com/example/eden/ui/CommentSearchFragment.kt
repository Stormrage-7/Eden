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
import com.example.eden.entities.Comment
import com.example.eden.ui.viewmodels.SearchViewModel

class CommentSearchFragment: Fragment() {
    private lateinit var fragmentCommentSearchBinding: FragmentCommentSearchBinding
    private lateinit var viewModel: SearchViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentCommentSearchBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_comment_search, container, false
        )
        viewModel = (activity as SearchableActivity).viewModel

        fragmentCommentSearchBinding.lifecycleOwner = this

        val adapter = CommentAdapter(activity as SearchableActivity, object: CommentAdapter.CommentClickListener {
            override fun onCommentClick(comment: Comment) {
                Intent(activity as SearchableActivity, PostDetailedActivity:: class.java).apply {
                    putExtra("CommentObject", comment)
                    startActivity(this)
                }
            }
        })

        fragmentCommentSearchBinding.rvComments.adapter = adapter
        fragmentCommentSearchBinding.rvComments.layoutManager = LinearLayoutManager(context)
        fragmentCommentSearchBinding.rvComments.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager(context).orientation
            )
        )

        viewModel.commentList.observe(activity as SearchableActivity) {
            if (it != null) {
                if (it.isEmpty()) {
                    fragmentCommentSearchBinding.rvComments.visibility = View.GONE
                    fragmentCommentSearchBinding.tempImgView.visibility = View.VISIBLE
                    fragmentCommentSearchBinding.tempTextView.visibility = View.VISIBLE
                } else {
                    fragmentCommentSearchBinding.rvComments.visibility = View.VISIBLE
                    fragmentCommentSearchBinding.tempImgView.visibility = View.GONE
                    fragmentCommentSearchBinding.tempTextView.visibility = View.GONE
                }
                adapter.updateCommentList(it)
            }
        }

        return fragmentCommentSearchBinding.root
    }
}