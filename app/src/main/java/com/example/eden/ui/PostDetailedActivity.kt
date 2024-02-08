package com.example.eden.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eden.database.AppRepository
import com.example.eden.ui.viewmodels.DetailedPostViewModel
import com.example.eden.ui.viewmodels.DetailedPostViewModelFactory
import com.example.eden.Eden
import com.example.eden.adapters.PostWithCommentsAdapter
import com.example.eden.databinding.ActivityDetailedPostViewBinding
import com.example.eden.dialogs.ConfirmationDialogFragment
import com.example.eden.entities.Comment
import com.example.eden.entities.Community
import com.example.eden.entities.Post
import com.example.eden.util.PostUriParser

class PostDetailedActivity: AppCompatActivity(),
    ConfirmationDialogFragment.ConfirmationDialogListener{

    private lateinit var activityDetailedPostViewBinding: ActivityDetailedPostViewBinding
    private lateinit var viewModel: DetailedPostViewModel
    private lateinit var repository: AppRepository
    private lateinit var factory: DetailedPostViewModelFactory
    private var postFound = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Inflation
        activityDetailedPostViewBinding = ActivityDetailedPostViewBinding.inflate(layoutInflater)
        setContentView(activityDetailedPostViewBinding.root)

        val application = application as Eden
        repository = application.repository

        factory = if (intent.hasExtra("PostObject")){
            val post = intent.getSerializableExtra("PostObject") as Post
            DetailedPostViewModelFactory(repository,
                post.postId,
                post.communityId,
                application)
        } else if (intent.hasExtra("CommentObject")) {
            val comment = intent.getSerializableExtra("CommentObject") as Comment
            DetailedPostViewModelFactory(repository,
                comment.postId,
                comment.communityId,
                application)
        } else {
            val uri = intent.getStringExtra("UriObject")
            val (postId, communityId) = PostUriParser.parse(uri!!)
            DetailedPostViewModelFactory(repository,
                postId,
                communityId,
                application)
        }

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
                postFound = true
                adapter.post = it
                adapter.notifyItemChanged(0)
            }
            else {
                postFound = false
//                Toast.makeText(this@PostDetailedActivity, "Post Doesn't Exist!", Toast.LENGTH_LONG).show()
                activityDetailedPostViewBinding.apply {
                    editButton.visibility = View.GONE
                    deleteButton.visibility = View.GONE
                    bottomCommentBar.visibility = View.GONE
                    rvComments.visibility = View.GONE
                    tempTextView.visibility = View.GONE
                    tempImgView.visibility = View.GONE
                    noPostTextView.visibility = View.VISIBLE
                    noPostImgView.visibility = View.VISIBLE
                }
            }
        }

        viewModel.community.observe(this) {
            if (it != null) {
                adapter.community = it
                adapter.notifyItemChanged(0)
            }
        }

        viewModel.commentList.observe(this) {
            it?.let {
                if (it.isEmpty() and postFound) {
                    activityDetailedPostViewBinding.apply {
                        tempImgView.visibility = View.VISIBLE
                        tempTextView.visibility = View.VISIBLE
                    }
                } else {
                    activityDetailedPostViewBinding.apply {
                        rvComments.updateLayoutParams<ConstraintLayout.LayoutParams> {
                            bottomToTop = bottomCommentBar.id
                        }
                        tempImgView.visibility = View.GONE
                        tempTextView.visibility = View.GONE
                    }
                }
                adapter.updateCommentList(it)
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
                        putExtra("CommunityId", it.communityId)
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
                val deleteConfirmationDialog = ConfirmationDialogFragment("Are you sure you want to delete this post?")
                deleteConfirmationDialog.show(supportFragmentManager, "DeleteConfirmationDialog")
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