package com.example.eden.ui

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eden.database.AppRepository
import com.example.eden.adapters.CommentAdapter
import com.example.eden.ui.viewmodels.DetailedPostViewModel
import com.example.eden.ui.viewmodels.DetailedPostViewModelFactory
import com.example.eden.Eden
import com.example.eden.R
import com.example.eden.adapters.CommunityWithPostsAdapter
import com.example.eden.adapters.PostWithCommentsAdapter
import com.example.eden.databinding.ActivityDetailedPostViewBinding
import com.example.eden.dialogs.DeleteConfirmationDialogFragment
import com.example.eden.dialogs.DiscardChangesDialogFragment
import com.example.eden.entities.Comment
import com.example.eden.entities.Community
import com.example.eden.entities.Post
import com.example.eden.enums.PostFilter
import com.example.eden.enums.VoteStatus

class PostDetailedActivity: AppCompatActivity(),
    DeleteConfirmationDialogFragment.DeleteConfirmationDialogListener{

    private lateinit var activityDetailedPostViewBinding: ActivityDetailedPostViewBinding
    private lateinit var viewModel: DetailedPostViewModel
    private lateinit var repository: AppRepository
    private lateinit var factory: DetailedPostViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Inflation
        activityDetailedPostViewBinding = ActivityDetailedPostViewBinding.inflate(layoutInflater)
        setContentView(activityDetailedPostViewBinding.root)

        val application = application as Eden
        val post = intent.getSerializableExtra("PostObject") as Post
        repository = application.repository
        factory = DetailedPostViewModelFactory(repository,
            post,
            intent.getSerializableExtra("CommunityObject") as Community,
            application)
        viewModel = ViewModelProvider(this, factory)[DetailedPostViewModel::class.java]

        val adapter = PostWithCommentsAdapter(context = this, object : PostWithCommentsAdapter.PostListener {

            override fun onCommunityClick(community: Community) {
                //TODO
            }

            override fun onUpvoteBtnClick() {
                viewModel.upvotePost()
            }

            override fun onDownvoteBtnClick() {
                viewModel.downvotePost()
            }
        })

        viewModel.post.observe(this) {
            if (it != null) {
                adapter.post = it
                adapter.notifyItemChanged(0)
            }
        }

        viewModel.community.observe(this) {
            it?.let {
                adapter.community = it
            }
        }

        viewModel.commentList.observe(this) {
            it.let {
                if (it.isEmpty()) {
                    activityDetailedPostViewBinding.apply {
                        tempImgView.visibility = View.VISIBLE
                        tempTextView.visibility = View.VISIBLE
                    }
                    adapter.updateCommentList(it)
                } else {
                    activityDetailedPostViewBinding.apply {
                        tempImgView.visibility = View.GONE
                        tempTextView.visibility = View.GONE
                    }
                    adapter.updateCommentList(it)
                }
            }
        }

        //POST DETAILS
        activityDetailedPostViewBinding.apply {

            backBtn.setOnClickListener {
                finish()
            }

            commentTextView.setOnClickListener {
                Intent(this@PostDetailedActivity, NewCommentActivity::class.java).apply {
                    viewModel.post.value?.let {
                        putExtra("PostTitle", it.title)
                        putExtra("PostId", it.postId)
                    }
                    startActivity(this)
                }
            }

            editButton.setOnClickListener {
                Intent(this@PostDetailedActivity, NewPostActivity::class.java).apply {
                    putExtra("Context", "PostDetailedActivity")
                    putExtra("PostObject", viewModel.post.value)
                    putExtra("CommunityObject", viewModel.community.value)
                    startActivity(this)
                }
            }

            deleteButton.setOnClickListener {
                val deleteConfirmationDialog = DeleteConfirmationDialogFragment()
                deleteConfirmationDialog.show(supportFragmentManager, "DeleteConfirmationDialogFragment")
            }
        }

        activityDetailedPostViewBinding.rvComments.adapter = adapter
        activityDetailedPostViewBinding.rvComments.layoutManager = LinearLayoutManager(this)
        activityDetailedPostViewBinding.rvComments.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager(this).orientation
            )
        )
    }

    override fun onDialogPositiveClick() {
        viewModel.deletePost()
        finish()
    }

    override fun onDialogNegativeClick() {}

}