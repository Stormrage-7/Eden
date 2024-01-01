package com.example.eden.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eden.database.AppRepository
import com.example.eden.ui.viewmodels.DetailedCommunityViewModel
import com.example.eden.ui.viewmodels.DetailedCommunityViewModelFactory
import com.example.eden.Eden
import com.example.eden.adapters.PostAdapter
import com.example.eden.R
import com.example.eden.databinding.ActivityDetailedCommunityViewBinding
import com.example.eden.entities.Community
import com.example.eden.entities.Post

class CommunityDetailedActivity: AppCompatActivity() {

    private lateinit var detailedCommunityViewBinding: ActivityDetailedCommunityViewBinding
    private lateinit var viewModel: DetailedCommunityViewModel
    private lateinit var repository: AppRepository
    private lateinit var factory: DetailedCommunityViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Inflation
        detailedCommunityViewBinding = ActivityDetailedCommunityViewBinding.inflate(layoutInflater)
        setContentView(detailedCommunityViewBinding.root)

        val application = application as Eden
        repository = application.repository
        factory = DetailedCommunityViewModelFactory(repository,
            intent.getSerializableExtra("CommunityObject") as Community,
            application)
        viewModel = ViewModelProvider(this, factory)[DetailedCommunityViewModel::class.java]

        detailedCommunityViewBinding.apply {
            backBtn.setOnClickListener {
                finish()
            }

            createPostButton.setOnClickListener {
                Intent(this@CommunityDetailedActivity, NewPostActivity::class.java).apply {
                    putExtra("Context", "CommunityDetailedActivity")
                    putExtra("CommunityObject", viewModel.community.value)
                    startActivity(this)
                }
            }
        }

        detailedCommunityViewBinding.joinButton.setOnClickListener {
            viewModel.onJoinClick()
            updateJoinStatus()
        }

        detailedCommunityViewBinding.editButton.setOnClickListener {
            Intent(this@CommunityDetailedActivity, NewCommunityActivity::class.java).apply {
                putExtra("Context", "CommunityDetailedActivity")
                putExtra("CommunityObject", viewModel.community.value)
                startActivity(this)
            }
        }

        val adapter = PostAdapter(context = this, object : PostAdapter.PostListener {
            override fun getCommunityIdFromPostId(position: Int): Int {
                return 1
            }

            override fun onCommunityClick(community: Community) {
                //TODO
            }
            override fun onPostClick(post: Post, community: Community) {
                Intent(this@CommunityDetailedActivity, PostDetailedActivity::class.java).apply {
                    putExtra("PostObject", post)
                    putExtra("CommunityObject", community)
                    startActivity(this)
                }
            }

            override fun onUpvoteBtnClick(post: Post) {
                viewModel.upvotePost(post)
            }

            override fun onDownvoteBtnClick(post: Post) {
                viewModel.downvotePost(post)
            }

            override fun onPostLongClick(position: Int) {
                TODO("Not yet implemented")
            }

        })

        viewModel.community.observe(this, Observer {
            detailedCommunityViewBinding.apply {
                if (it.isCustomImage) imageViewCommunity.setImageURI(
                    Uri.parse(
                        it.imageUri
                    )
                )
                else imageViewCommunity.setImageResource(it.imageUri.toInt())
                textViewCommunityName.text = it.communityName
                textViewCommunityDescription.text = it.description
                updateJoinStatus()
                detailedCommunityViewBinding.rvPosts.adapter = adapter
                detailedCommunityViewBinding.rvPosts.layoutManager = LinearLayoutManager(this@CommunityDetailedActivity)
                detailedCommunityViewBinding.rvPosts.addItemDecoration(
                    DividerItemDecoration(
                        this@CommunityDetailedActivity,
                        LinearLayoutManager(this@CommunityDetailedActivity).orientation
                    )
                )
                adapter.currentCommunity = it
            }
        })

        viewModel.postList.observe(this, Observer {
            it.let {
                if(it.isEmpty()){
                    detailedCommunityViewBinding.rvPosts.visibility = View.GONE
                    detailedCommunityViewBinding.tempImgView.visibility = View.VISIBLE
                    detailedCommunityViewBinding.tempTextView.visibility = View.VISIBLE
                    adapter!!.updatePostList(it)
                }
                else {
                    detailedCommunityViewBinding.rvPosts.visibility = View.VISIBLE
                    detailedCommunityViewBinding.tempImgView.visibility = View.GONE
                    detailedCommunityViewBinding.tempTextView.visibility = View.GONE
                    adapter!!.updatePostList(it)
                    Log.i("Inside PostList Observer", it.toString())
                    Log.i("Inside PostList Observer", adapter.postList.toString())
                }
            }
        })

    }

    private fun updateJoinStatus() {
        detailedCommunityViewBinding.apply {
            if (viewModel.community.value!!.isJoined){
                joinButton.text = "Joined"
                joinButton.backgroundTintList = ColorStateList.valueOf(
                    ResourcesCompat.getColor(resources, R.color.grey, null)
                )
            }
            else{
                joinButton.text = "Join"
                joinButton.backgroundTintList = ColorStateList.valueOf(
                    ResourcesCompat.getColor(resources, R.color.azure, null)
                )
            }
        }
    }

}