package com.example.eden.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.size
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eden.database.AppRepository
import com.example.eden.ui.viewmodels.DetailedCommunityViewModel
import com.example.eden.ui.viewmodels.DetailedCommunityViewModelFactory
import com.example.eden.Eden
import com.example.eden.R
import com.example.eden.adapters.CommunityWithPostsAdapter
import com.example.eden.adapters.PostAdapter
import com.example.eden.databinding.ActivityDetailedCommunityViewBinding
import com.example.eden.databinding.BottomSheetPostFilterBinding
import com.example.eden.databinding.ItemDetailedCommunityBinding
import com.example.eden.dialogs.ConfirmationDialogFragment
import com.example.eden.entities.Community
import com.example.eden.entities.Post
import com.example.eden.enums.CollapsingToolBarState
import com.example.eden.enums.PostFilter
import com.example.eden.util.PostUriGenerator
import com.example.eden.util.UriValidation
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.divider.MaterialDividerItemDecoration
import kotlin.math.abs

class CommunityDetailedActivity: ConfirmationDialogFragment.ConfirmationDialogListener,
    AppCompatActivity() {

    private var collapsingToolBarState = CollapsingToolBarState.EXPANDED
    private lateinit var detailedCommunityViewBinding: ActivityDetailedCommunityViewBinding
    private lateinit var viewModel: DetailedCommunityViewModel
    private lateinit var repository: AppRepository
    private lateinit var factory: DetailedCommunityViewModelFactory
    private lateinit var bottomSheetDialog: BottomSheetDialog

    @SuppressLint("ResourceAsColor")
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

        setSupportActionBar(detailedCommunityViewBinding.detailedCommunityToolbar)

        detailedCommunityViewBinding.communityAppBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) == appBarLayout.totalScrollRange){
                //Collapsed
                collapsingToolBarState = CollapsingToolBarState.COLLAPSED
                detailedCommunityViewBinding.textViewCommunityDescription.visibility = View.INVISIBLE
                detailedCommunityViewBinding.detailedCommunityToolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.arrow_back_white_24)
                invalidateOptionsMenu()
            }
            else if (verticalOffset == 0) {
                //Expanded
                collapsingToolBarState = CollapsingToolBarState.EXPANDED
                detailedCommunityViewBinding.textViewCommunityDescription.visibility = View.VISIBLE
                detailedCommunityViewBinding.detailedCommunityToolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_24)
                detailedCommunityViewBinding.detailedCommunityToolbar.popupTheme
                invalidateOptionsMenu()
            }
            else{
                //In-Between
                collapsingToolBarState = CollapsingToolBarState.IN_BETWEEN
                detailedCommunityViewBinding.textViewCommunityDescription.visibility = View.VISIBLE
                detailedCommunityViewBinding.detailedCommunityToolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.arrow_back_white_24)
                invalidateOptionsMenu()
            }
        }

        // INITIALIZING ADAPTER FOR RECYCLERVIEW
        val adapter = PostAdapter(context = this, object : PostAdapter.PostListener {

            override fun onPostClick(post: Post) {
                Intent(this@CommunityDetailedActivity, PostDetailedActivity::class.java).apply {
                    putExtra("PostObject", post)
                    startActivity(this)
                }
            }

            override fun onCommunityClick(community: Community) {
            }

            override fun onUpvoteBtnClick(post: Post) {
                viewModel.upvotePost(post)
            }

            override fun onDownvoteBtnClick(post: Post) {
                viewModel.downvotePost(post)
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

        // INITIALIZING ON-CLICK LISTENERS FOR BUTTONS
        detailedCommunityViewBinding.apply {
            createPostButton.setOnClickListener {
                createPost()
            }

            joinButton.setOnClickListener {
                if (viewModel.community.value?.isJoined == true){
                    val discardChangesDialog = ConfirmationDialogFragment("Are you sure you want to leave this community?")
                    discardChangesDialog.show(supportFragmentManager, "LeaveCommunityDialog")
                }
                else viewModel.onJoinClick()
            }

//            communityCardEditButton.setOnClickListener {
//                editCommunity()
//            }

            detailedCommunityToolbar.setNavigationOnClickListener { finish() }

            // FILTER BUTTON
            filterButton.setOnClickListener {
                val dialogViewBinding = BottomSheetPostFilterBinding.inflate(LayoutInflater.from(this@CommunityDetailedActivity))
                bottomSheetDialog = BottomSheetDialog(this@CommunityDetailedActivity)
                bottomSheetDialog.setContentView(dialogViewBinding.root)

                dialogViewBinding.filterHot.setOnClickListener {
                    viewModel.filter = PostFilter.HOT
                    filterButton.apply {
                        text = viewModel.filter.text
                        setCompoundDrawablesWithIntrinsicBounds(viewModel.filter.imgSrc, 0, R.drawable.arrow_down_24, 0)
                    }
                    adapter.updateFilter(viewModel.filter)
                    bottomSheetDialog.dismiss()
                }
                dialogViewBinding.filterTop.setOnClickListener {
                    viewModel.filter = PostFilter.TOP
                    filterButton.apply {
                        text = viewModel.filter.text
                        setCompoundDrawablesWithIntrinsicBounds(viewModel.filter.imgSrc, 0, R.drawable.arrow_down_24, 0)
                    }
                    adapter.updateFilter(viewModel.filter)
                    bottomSheetDialog.dismiss()
                }
                dialogViewBinding.filterOld.setOnClickListener {
                    viewModel.filter = PostFilter.OLDEST
                    filterButton.apply {
                        text = viewModel.filter.text
                        setCompoundDrawablesWithIntrinsicBounds(viewModel.filter.imgSrc, 0, R.drawable.arrow_down_24, 0)
                    }
                    adapter.updateFilter(viewModel.filter)
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
            }
        }

        detailedCommunityViewBinding.apply {
            rvDetailedCommunity.adapter = adapter
            rvDetailedCommunity.addItemDecoration(
                MaterialDividerItemDecoration(
                    this@CommunityDetailedActivity,
                    LinearLayoutManager(this@CommunityDetailedActivity).orientation
                )
            )
        }

        viewModel.community.observe(this) {
            detailedCommunityViewBinding.apply {
                if (it.isCustomImage) {
                    if (UriValidation.validate(this@CommunityDetailedActivity, it.imageUri)) imageViewCommunity.setImageURI(
                        Uri.parse(it.imageUri))
                    else imageViewCommunity.setImageResource(R.drawable.icon_logo)
                }
                else imageViewCommunity.setImageResource(it.imageUri.toInt())

                textViewCommunityName.text = it.communityName
                detailedCommunityViewBinding.detailedCommunityToolbar.title = it.communityName
                detailedCommunityViewBinding.detailedCommunityToolbar.setTitleTextColor(R.color.white)
                textViewCommunityDescription.text = it.description
                updateJoinStatus(it.isJoined)
            }
        }

        viewModel.postList.observe(this) {
            it.let {
                if (it.isEmpty()) {
                    detailedCommunityViewBinding.tempImgView.visibility = View.VISIBLE
                    detailedCommunityViewBinding.tempTextView.visibility = View.VISIBLE
                } else {
                    detailedCommunityViewBinding.tempImgView.visibility = View.GONE
                    detailedCommunityViewBinding.tempTextView.visibility = View.GONE
                }
                adapter.updatePostList(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return when (collapsingToolBarState) {
            CollapsingToolBarState.COLLAPSED -> { menuInflater.inflate(R.menu.community_topbar_menu, menu)
            true}
            CollapsingToolBarState.IN_BETWEEN -> {false}
            CollapsingToolBarState.EXPANDED -> { menuInflater.inflate(R.menu.community_toolbar_menu_hidden, menu)
            true}
        }
//        if (menu != null) {
//            if (topBarMenuHidden) for (index in 0 until menu.size){
//                if (index != 0) menu.getItem(index).isVisible = false
//            }
//            else for (index in 0 until menu.size) menu.getItem(index).isVisible = true
//        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toolbarEditButton -> {
                editCommunity()
                true
            }
            R.id.toolbarCreatePostButton -> {
                createPost()
                true
            }
            R.id.toolbarJoinButton -> {
                viewModel.onJoinClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDialogPositiveClick() {
        viewModel.onJoinClick()
    }

    override fun onDialogNegativeClick() {
    }
    private fun updateJoinStatus(isJoined: Boolean) {
        detailedCommunityViewBinding.apply {
            if (isJoined){
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

    private fun editCommunity(){
        Intent(this@CommunityDetailedActivity, NewCommunityActivity::class.java).apply {
            putExtra("Context", "CommunityDetailedActivity")
            putExtra("CommunityObject", viewModel.community.value)
            startActivity(this)
        }
    }

    private fun createPost(){
        Intent(this@CommunityDetailedActivity, NewPostActivity::class.java).apply {
            putExtra("Context", "CommunityDetailedActivity")
            putExtra("CommunityObject", viewModel.community.value)
            startActivity(this)
        }
    }

}