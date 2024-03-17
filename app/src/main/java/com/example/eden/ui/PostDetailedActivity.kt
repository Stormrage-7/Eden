package com.example.eden.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
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
import com.example.eden.models.CommentModel
import com.example.eden.models.PostModel
import com.example.eden.util.PostUriGenerator
import com.example.eden.util.PostUriParser
import com.google.android.material.divider.MaterialDividerItemDecoration

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
            val post = intent.getSerializableExtra("PostObject") as PostModel
            DetailedPostViewModelFactory(repository,
                post.postId,
                post.communityId,
                application)
        } else if (intent.hasExtra("CommentObject")) {
            val comment = intent.getSerializableExtra("CommentObject") as CommentModel
            DetailedPostViewModelFactory(repository,
                comment.postId,
                comment.communityId,
                application)
        } else {
            val uri = intent.getStringExtra("Uri")
            val (postId, communityId) = PostUriParser.parse(uri!!)
            DetailedPostViewModelFactory(repository,
                postId,
                communityId,
                application)
        }

        viewModel = ViewModelProvider(this, factory)[DetailedPostViewModel::class.java]

        val adapter = PostWithCommentsAdapter(context = this, object : PostWithCommentsAdapter.PostListener {

            override fun onCommunityClick(community: Community) {
            }

            override fun onUserClick(userId: Int) {
                openProfile(userId)
            }

            override fun onUpvoteBtnClick() {
                viewModel.upvotePost()
            }

            override fun onDownvoteBtnClick() {
                viewModel.downvotePost()
            }

            override fun onBookmarkClick() {
                viewModel.bookmarkPost()
            }

            override fun commentUpvoteButtonClick(comment: CommentModel) {
                viewModel.upvoteComment(comment)
            }

            override fun commentDownvoteButtonClick(comment: CommentModel) {
                viewModel.downvoteComment(comment)
            }

            override fun onShareClick(postId: Int, communityId: Int) {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, PostUriGenerator.generate(postId, communityId))
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
        })

        viewModel.post.observe(this) {

            if (it != null) {
                postFound = true
                activityDetailedPostViewBinding.rvComments.visibility = View.VISIBLE
                adapter.post = it
                adapter.notifyItemChanged(0)
                if (it.posterId != application.userId){
                    activityDetailedPostViewBinding.apply {
                        editButton.visibility = View.GONE
                        deleteButton.visibility = View.GONE
                    }
                }
            }
            else {
                postFound = false
                activityDetailedPostViewBinding.apply {
                    editButton.visibility = View.GONE
                    deleteButton.visibility = View.GONE
                    bottomCommentBar.visibility = View.GONE
                    rvComments.visibility = View.GONE
                    noPostTextView.visibility = View.VISIBLE
                    noPostImgView.visibility = View.VISIBLE
                }
            }
        }

        viewModel.community.observe(this) {
            it?.let{
                adapter.community = it
                adapter.notifyItemChanged(0)
            }
        }

        viewModel.commentList.observe(this) {
            it?.let {
                adapter.updateCommentList(it)
            }
        }

        viewModel.userList.observe(this) {
            it?.let {
                adapter.updateUserList(it)
            }
            adapter.notifyDataSetChanged()
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
        val materialDecoration = MaterialDividerItemDecoration(
            this,
            LinearLayoutManager(this).orientation
        )
        materialDecoration.isLastItemDecorated = false
        activityDetailedPostViewBinding.rvComments.addItemDecoration(materialDecoration)

    }

    override fun onDialogPositiveClick() {
        viewModel.deletePost()
        finish()
    }

    override fun onDialogNegativeClick() {}

    private fun openProfile(userId: Int){
        Intent(this, UserProfileActivity::class.java).apply {
            putExtra("UserId", userId)
            startActivity(this)
        }
    }

}