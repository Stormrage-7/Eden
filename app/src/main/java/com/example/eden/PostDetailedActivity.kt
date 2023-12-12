package com.example.eden

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
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
    private lateinit var post: Post
    private lateinit var community: Community

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
            if(viewModel.community.isCustomImage) imageViewCommunity.setImageURI(Uri.parse(viewModel.community.imageUri))
            else imageViewCommunity.setImageResource(viewModel.community.imageUri.toInt())
            textViewCommunityName.text = viewModel.community.communityName
            textViewTitle.text = viewModel.post.title

            if(viewModel.post.containsImage){
                imageViewPost.visibility = View.VISIBLE
                imageViewPost.setImageURI(Uri.parse(viewModel.post.imageUri))
                imageViewPost.scaleType = ImageView.ScaleType.FIT_XY
            }
            else imageViewPost.visibility = View.GONE

            //BODY TEXT
            if(viewModel.post.bodyText.isEmpty()){
                textViewDescription.visibility = View.GONE
            }
            else{
                textViewDescription.visibility = View.VISIBLE
                textViewDescription.text = viewModel.post.bodyText
            }
            updateVoteSystem()

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
                viewModel.addComment(Comment(0, text = commentText, postId = viewModel.post.postId))
                commentEditText.text.clear()
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

        viewModel.commentList.observe(this, Observer {
            it.let {
                if(it.isEmpty()){
                    activityDetailedPostViewBinding.rvComments.visibility = View.GONE
                    activityDetailedPostViewBinding.tempImgView.visibility = View.VISIBLE
                    activityDetailedPostViewBinding.tempTextView.visibility = View.VISIBLE
                    adapter!!.updateCommentList(it)
                }
                else {
                    activityDetailedPostViewBinding.rvComments.visibility = View.VISIBLE
                    activityDetailedPostViewBinding.tempImgView.visibility = View.GONE
                    activityDetailedPostViewBinding.tempTextView.visibility = View.GONE
                    adapter!!.updateCommentList(it)
                }
            }
        })
    }

    private fun updateVoteSystem() {
        activityDetailedPostViewBinding.apply {
            textViewVoteCounter.text = viewModel.post.voteCounter.toString()

            when (viewModel.post.voteStatus) {
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