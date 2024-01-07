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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eden.database.AppRepository
import com.example.eden.adapters.CommentAdapter
import com.example.eden.ui.viewmodels.DetailedPostViewModel
import com.example.eden.ui.viewmodels.DetailedPostViewModelFactory
import com.example.eden.Eden
import com.example.eden.R
import com.example.eden.databinding.ActivityDetailedPostViewBinding
import com.example.eden.entities.Comment
import com.example.eden.entities.Community
import com.example.eden.entities.Post
import com.example.eden.enums.VoteStatus

class PostDetailedActivity: AppCompatActivity(){

    private lateinit var activityDetailedPostViewBinding: ActivityDetailedPostViewBinding
    private lateinit var viewModel: DetailedPostViewModel
    private lateinit var repository: AppRepository
    private lateinit var factory: DetailedPostViewModelFactory
    private var mScrollY = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Inflation
        activityDetailedPostViewBinding = ActivityDetailedPostViewBinding.inflate(layoutInflater)
        setContentView(activityDetailedPostViewBinding.root)

        val application = application as Eden
        repository = application.repository
        factory = DetailedPostViewModelFactory(repository,
            intent.getSerializableExtra("PostObject") as Post,
            intent.getSerializableExtra("CommunityObject") as Community,
            application)
        viewModel = ViewModelProvider(this, factory)[DetailedPostViewModel::class.java]

        //POST DETAILS
        activityDetailedPostViewBinding.apply {

            postCommentButton.isEnabled = false

            likeBtn.setOnClickListener { viewModel.upvotePost()
                updateVoteSystem()
            }
            dislikeBtn.setOnClickListener { viewModel.downvotePost()
                updateVoteSystem()
            }
            backBtn.setOnClickListener {
                finish()
            }

            commentEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("Not yet implemented")
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (p0?.trim()?.isNotEmpty() == true) {
                        postCommentButton.isEnabled = true
                        postCommentButton.backgroundTintList = ColorStateList.valueOf(
                            ResourcesCompat.getColor(resources, R.color.azure, null)
                        )
                    } else {
                        postCommentButton.isEnabled = false
                        postCommentButton.backgroundTintList = ColorStateList.valueOf(
                            ResourcesCompat.getColor(resources, R.color.grey, null)
                        )
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
//                TODO("Not yet implemented")
                }
            })

            postCommentButton.setOnClickListener {
                val commentText = commentEditText.text.toString()
                viewModel.addComment(Comment(0, text = commentText, postId = viewModel.post.value!!.postId))
                commentEditText.text.clear()
                commentEditText.clearFocus()
                val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(commentEditText.windowToken, 0)
            }

            editButton.setOnClickListener {
                Intent(this@PostDetailedActivity, NewPostActivity::class.java).apply {
                    putExtra("Context", "PostDetailedActivity")
                    putExtra("PostObject", viewModel.post.value)
                    startActivity(this)
                }
            }

            deleteButton.setOnClickListener {
                viewModel.deletePost()
                finish()
            }
        }

        val adapter = CommentAdapter(context = this)
        activityDetailedPostViewBinding.rvComments.adapter = adapter
        activityDetailedPostViewBinding.rvComments.layoutManager = LinearLayoutManager(this)
        activityDetailedPostViewBinding.rvComments.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager(this).orientation
            )
        )

        viewModel.post.observe(this) {
            activityDetailedPostViewBinding.apply {
                textViewTitle.text = it.title
                if (it.containsImage) {
                    imageViewPost.visibility = View.VISIBLE
                    imageViewPost.setImageURI(Uri.parse(it.imageUri))
                    imageViewPost.scaleType = ImageView.ScaleType.CENTER_CROP
                } else imageViewPost.visibility = View.GONE

                //BODY TEXT
                if (it.bodyText.isEmpty()) {
                    textViewDescription.visibility = View.GONE
                } else {
                    textViewDescription.visibility = View.VISIBLE
                    textViewDescription.text = it.bodyText
                }
                updateVoteSystem()
            }
        }

        viewModel.community.observe(this) {
            activityDetailedPostViewBinding.apply {
                if (it.isCustomImage) imageViewCommunity.setImageURI(
                    Uri.parse(
                        it.imageUri
                    )
                )
                else imageViewCommunity.setImageResource(it.imageUri.toInt())
                textViewCommunityName.text = it.communityName
            }
        }

        viewModel.commentList.observe(this) {
            it.let {
                if (it.isEmpty()) {
                    activityDetailedPostViewBinding.apply {
                        rvComments.visibility = View.GONE
                        tempImgView.visibility = View.VISIBLE
                        tempTextView.visibility = View.VISIBLE
                    }
                    adapter.updateCommentList(it)
                } else {
                    activityDetailedPostViewBinding.apply {
                        rvComments.visibility = View.VISIBLE
                        tempImgView.visibility = View.GONE
                        tempTextView.visibility = View.GONE
                    }
                    adapter.updateCommentList(it)
                }
            }
        }

    //        activityDetailedPostViewBinding.rvComments.addOnScrollListener(object : OnScrollListener(){
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                mScrollY += dy.toFloat()
//                mScrollY = max(mScrollY, 0F)
//
//                activityDetailedPostViewBinding.postDetails.translationY = min(-mScrollY, 0F)
//            }
//        })
    }

    private fun updateVoteSystem() {
        activityDetailedPostViewBinding.apply {
            textViewVoteCounter.text = viewModel.post.value!!.voteCounter.toString()

            when (viewModel.post.value!!.voteStatus) {
                VoteStatus.UPVOTED -> {
                    likeBtn.setImageResource(R.drawable.upvote_circle_up_green_24)
                    dislikeBtn.setImageResource(R.drawable.downvote_circle_down_24)
                    textViewVoteCounter.setTextColor(
                        ContextCompat.getColor(
                            this@PostDetailedActivity,
                            R.color.green
                        )
                    )
                }

                VoteStatus.DOWNVOTED -> {
                    dislikeBtn.setImageResource(R.drawable.downvote_circle_down_red_24)
                    likeBtn.setImageResource(R.drawable.upvote_circle_up_24)
                    textViewVoteCounter.setTextColor(
                        ContextCompat.getColor(
                            this@PostDetailedActivity,
                            R.color.red
                        )
                    )
                }

                VoteStatus.NONE -> {
                    likeBtn.setImageResource(R.drawable.upvote_circle_up_24)
                    dislikeBtn.setImageResource(R.drawable.downvote_circle_down_24)
                    textViewVoteCounter.setTextColor(
                        ContextCompat.getColor(
                            this@PostDetailedActivity,
                            R.color.black
                        )
                    )
                }
            }
        }
    }

}