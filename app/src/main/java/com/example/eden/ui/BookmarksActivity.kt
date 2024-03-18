package com.example.eden.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eden.Eden
import com.example.eden.adapters.PostAdapter
import com.example.eden.databinding.ActivityUserBookmarksBinding
import com.example.eden.models.CommunityModel
import com.example.eden.models.PostModel
import com.example.eden.ui.viewmodels.BookmarksViewModel
import com.example.eden.ui.viewmodels.BookmarksViewModelFactory
import com.example.eden.util.PostUriGenerator

class BookmarksActivity: AppCompatActivity() {
    private lateinit var binding: ActivityUserBookmarksBinding
    lateinit var viewModel: BookmarksViewModel
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityUserBookmarksBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val application = application as Eden
        val factory = BookmarksViewModelFactory(application.repository, application)
        viewModel = ViewModelProvider(this, factory)[BookmarksViewModel::class.java]

        setSupportActionBar(binding.toolbar)

        val adapter = PostAdapter(this, object : PostAdapter.PostListener {

            override fun onCommunityClick(community: CommunityModel) {
                Intent(this@BookmarksActivity, CommunityDetailedActivity::class.java).apply {
                    putExtra("CommunityObject", community)
                    startActivity(this)
                }
            }
            override fun onPostClick(post: PostModel) {
                Intent(this@BookmarksActivity, PostDetailedActivity::class.java).apply {
                    putExtra("PostObject", post)
                    startActivity(this)
                }
            }

            override fun onUserClick(userId: Int) { openProfile(userId) }
            override fun onUpvoteBtnClick(post: PostModel) { viewModel.upvotePost(post) }
            override fun onDownvoteBtnClick(post: PostModel) { viewModel.downvotePost(post) }
            override fun onBookmarkClick(post: PostModel) { viewModel.bookmarkPost(post) }

            override fun onShareBtnClick(postId: Int, communityId: Int) {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, PostUriGenerator.generate(postId, communityId))
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }

            override fun scrollToTop() {
                binding.rvPosts.smoothScrollToPosition(0)
            }
        })

        binding.rvPosts.adapter = adapter
        binding.rvPosts.layoutManager = LinearLayoutManager(this)
        binding.rvPosts.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager(this).orientation
            )
        )

        viewModel.communityList.observe(this) {
            it?.let {
                adapter.updateCommunityList(it)
            }
        }

        viewModel.userList.observe(this){
            it?.let { adapter.updateUserList(it) }
        }

        viewModel.postList.observe(this){
            it?.let {
                if(it.isEmpty()){
                    binding.rvPosts.visibility = View.GONE
                    binding.tempImgView.visibility = View.VISIBLE
                    binding.tempTextView.visibility = View.VISIBLE
                }
                else {
                    binding.rvPosts.visibility = View.VISIBLE
                    binding.tempImgView.visibility = View.GONE
                    binding.tempTextView.visibility = View.GONE
                }
                adapter.updatePostList(it)
            }
        }

        binding.toolbar.setNavigationOnClickListener{ finish() }
    }

    private fun openProfile(userId: Int){
        Intent(this, UserProfileActivity::class.java).apply {
            putExtra("UserId", userId)
            startActivity(this)
        }
    }

}