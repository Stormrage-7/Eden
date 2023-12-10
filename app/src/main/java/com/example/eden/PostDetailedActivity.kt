package com.example.eden

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.eden.databinding.ActivityDetailedPostViewBinding
import com.example.eden.entities.Community
import com.example.eden.entities.Post
import com.example.eden.enums.VoteStatus

class PostDetailedActivity: AppCompatActivity(){

    private lateinit var activityDetailedPostViewBinding: ActivityDetailedPostViewBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var repository: AppRepository
    private lateinit var factory: HomeViewModelFactory
    private lateinit var post: Post
    private lateinit var community: Community

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Inflation
        activityDetailedPostViewBinding = ActivityDetailedPostViewBinding.inflate(layoutInflater)
        setContentView(activityDetailedPostViewBinding.root)

        val application = application as Eden
        repository = application.repository
        factory = HomeViewModelFactory(repository, application)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        post = intent.getSerializableExtra("PostObject") as Post
        community = intent.getSerializableExtra("CommunityObject") as Community

        //POST DETAILS
        activityDetailedPostViewBinding.apply {
            if(community.isCustomImage) imageViewCommunity.setImageURI(Uri.parse(community.imageUri))
            else imageViewCommunity.setImageResource(community.imageUri.toInt())
            textViewCommunityName.text = community.communityName
            textViewTitle.text = post.title
            textViewDescription.text = post.bodyText
            textViewVoteCounter.text = post.voteCounter.toString()

            when(post.voteStatus){
                VoteStatus.UPVOTED -> TODO()
                VoteStatus.DOWNVOTED -> TODO()
                VoteStatus.NONE -> TODO()
            }

            likeBtn.setOnClickListener { viewModel.upvotePost(post) }
            dislikeBtn.setOnClickListener { viewModel.downvotePost(post) }
            shareBtn.isEnabled = false

        }

//        val adapter = context?.let {
//            PostAdapter(context = it, object : PostAdapter.PostListener{
//                })}
//        fragmentHomeBinding.rvPosts.adapter = adapter
//        fragmentHomeBinding.rvPosts.layoutManager = LinearLayoutManager(context)
//        fragmentHomeBinding.rvPosts.addItemDecoration(
//            DividerItemDecoration(
//                context,
//                LinearLayoutManager(context).orientation
//            )
//        )


    }

}