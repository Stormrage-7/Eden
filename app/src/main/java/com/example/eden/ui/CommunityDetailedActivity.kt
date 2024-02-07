package com.example.eden.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eden.database.AppRepository
import com.example.eden.ui.viewmodels.DetailedCommunityViewModel
import com.example.eden.ui.viewmodels.DetailedCommunityViewModelFactory
import com.example.eden.Eden
import com.example.eden.R
import com.example.eden.adapters.CommunityWithPostsAdapter
import com.example.eden.databinding.ActivityDetailedCommunityViewBinding
import com.example.eden.databinding.BottomSheetPostFilterBinding
import com.example.eden.dialogs.ConfirmationDialogFragment
import com.example.eden.entities.Community
import com.example.eden.entities.Post
import com.example.eden.enums.PostFilter
import com.example.eden.util.PostUriGenerator
import com.google.android.material.bottomsheet.BottomSheetDialog

class CommunityDetailedActivity: ConfirmationDialogFragment.ConfirmationDialogListener,
    AppCompatActivity() {

    private lateinit var detailedCommunityViewBinding: ActivityDetailedCommunityViewBinding
    private lateinit var viewModel: DetailedCommunityViewModel
    private lateinit var repository: AppRepository
    private lateinit var factory: DetailedCommunityViewModelFactory
    private lateinit var bottomSheetDialog: BottomSheetDialog

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

        detailedCommunityViewBinding.editButton.setOnClickListener {
            Intent(this@CommunityDetailedActivity, NewCommunityActivity::class.java).apply {
                putExtra("Context", "CommunityDetailedActivity")
                putExtra("CommunityObject", viewModel.community.value)
                startActivity(this)
            }

        }

        detailedCommunityViewBinding.backBtn.setOnClickListener { finish() }

        val adapter = CommunityWithPostsAdapter(context = this, object : CommunityWithPostsAdapter.PostListener {

            override fun onCommunityClick(community: Community) {
                //TODO
            }
            override fun onPostClick(post: Post, community: Community) {
                Intent(this@CommunityDetailedActivity, PostDetailedActivity::class.java).apply {
                    putExtra("PostObject", post)
                    startActivity(this)
                }
            }

            override fun onUpvoteBtnClick(post: Post) {
                viewModel.upvotePost(post)
            }

            override fun onDownvoteBtnClick(post: Post) {
                viewModel.downvotePost(post)
            }

            override fun setFilter(filter: PostFilter) {
                viewModel.filter = filter
            }

            override fun onFilterClick() {
                TODO("Not yet implemented")
            }

            override fun onJoinClick() {
                if (viewModel.community.value?.isJoined == true){
                    val leaveCommunityConfirmationDialog = ConfirmationDialogFragment("Are you sure you want to leave this Community?")
                    leaveCommunityConfirmationDialog.show(supportFragmentManager, "LeaveCommunityConfirmationDialog")
                }
                else viewModel.onJoinClick()
            }

            override fun onCreateClick() {
                Intent(this@CommunityDetailedActivity, NewPostActivity::class.java).apply {
                    putExtra("Context", "CommunityDetailedActivity")
                    putExtra("CommunityObject", viewModel.community.value)
                    startActivity(this)
                }
            }

            override fun onShareBtnClick(postId: Int, communityId: Int) {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, PostUriGenerator.generate(postId, communityId))
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
        })
        adapter.resources = resources
        detailedCommunityViewBinding.apply {
            rvDetailedCommunity.adapter = adapter
            rvDetailedCommunity.layoutManager = LinearLayoutManager(this@CommunityDetailedActivity)
            rvDetailedCommunity.addItemDecoration(
                DividerItemDecoration(
                    this@CommunityDetailedActivity,
                    LinearLayoutManager(this@CommunityDetailedActivity).orientation
                )
            )
        }

        viewModel.community.observe(this) {
            adapter.currentCommunity = it
            adapter.notifyDataSetChanged()
        }

        viewModel.postList.observe(this) {
            it.let {
                if (it.isEmpty()) {
                    detailedCommunityViewBinding.tempImgView.visibility = View.VISIBLE
                    detailedCommunityViewBinding.tempTextView.visibility = View.VISIBLE
                    adapter.updatePostList(it)
                } else {
                    detailedCommunityViewBinding.tempImgView.visibility = View.GONE
                    detailedCommunityViewBinding.tempTextView.visibility = View.GONE
                    adapter.updatePostList(it)
                }
            }
        }

    }

    override fun onDialogPositiveClick() {
        viewModel.onJoinClick()
    }

    override fun onDialogNegativeClick() {
    }


}