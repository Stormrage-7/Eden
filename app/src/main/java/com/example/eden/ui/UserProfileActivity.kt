package com.example.eden.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.eden.Eden
import com.example.eden.R
import com.example.eden.adapters.UserProfileViewPagerAdapter
import com.example.eden.database.AppDatabase
import com.example.eden.database.AppRepository
import com.example.eden.databinding.ActivityUserProfileBinding
import com.example.eden.enums.CollapsingToolBarState
import com.example.eden.ui.viewmodels.ProfileViewModel
import com.example.eden.ui.viewmodels.ProfileViewModelFactory
import com.example.eden.util.UriValidator
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.math.abs

class UserProfileActivity: AppCompatActivity() {

    private var collapsingToolBarState = CollapsingToolBarState.EXPANDED
    private lateinit var binding: ActivityUserProfileBinding
    lateinit var viewModel: ProfileViewModel
    private lateinit var database: AppDatabase
    private lateinit var repository: AppRepository
    private lateinit var factory: ProfileViewModelFactory

    @SuppressLint("ResourceAsColor", "LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(applicationContext as Eden)
        repository = AppRepository(database.edenDao())
        factory = ProfileViewModelFactory(repository, intent.getIntExtra("UserId", -1), application as Eden)
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        Log.i("UserTitle outside 1", supportActionBar?.title.toString())
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        Log.i("UserTitle outside 2", supportActionBar?.title.toString())

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.imageViewProfileHeader.scaleType = ImageView.ScaleType.CENTER_CROP

        val viewPagerAdapter = UserProfileViewPagerAdapter(supportFragmentManager, lifecycle)
        binding.userProfileViewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.userProfileViewPager){ tab, position ->
            when(position){
                0 -> tab.text = "Posts"
                1 -> tab.text = "Comments"
                2 -> tab.text = "About"
            }
        }.attach()

        viewModel.user.observe(this) { user ->
            user?.let {
                Log.i("UserTitle inside let", supportActionBar?.title.toString())
                binding.apply {
                    Log.i("UserTitle inside apply 1", supportActionBar?.title.toString())
                    binding.toolbar.title = user.username
                    Log.i("UserTitle inside apply 2", supportActionBar?.title.toString())
                    if (user.isCustomImage and UriValidator.validate(
                            this@UserProfileActivity,
                            user.profileImageUri
                        )
                    ) {
                        imageViewProfileHeader.setImageURI(
                            Uri.parse(user.profileImageUri)
                        )
//                    imageViewProfileHeader.scaleType = ImageView.ScaleType.CENTER_CROP
                    } else {
                        if (user.userId in 1..3) imageViewProfileHeader.setImageResource(user.profileImageUri.toInt())
                        else imageViewProfileHeader.setImageResource(R.drawable.ic_avatar)
                    }

                    textViewUserNameProfileHeader.text = user.username
                    textViewEmailProfileHeader.text = user.email
                }
            }
        }

        binding.appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) == appBarLayout.totalScrollRange){
                //Collapsed
                collapsingToolBarState = CollapsingToolBarState.COLLAPSED
                binding.apply {
                    toolbar.navigationIcon = ContextCompat.getDrawable(this@UserProfileActivity, R.drawable.arrow_back_white_24)
                }
            }
            else if (verticalOffset == 0) {
                //Expanded
                collapsingToolBarState = CollapsingToolBarState.EXPANDED
                binding.apply {
                    toolbar.navigationIcon = ContextCompat.getDrawable(this@UserProfileActivity, R.drawable.ic_arrow_back_24)
                }
            }
            else{
                //In-Between
                collapsingToolBarState = CollapsingToolBarState.IN_BETWEEN
                binding.apply {
                    toolbar.navigationIcon = ContextCompat.getDrawable(this@UserProfileActivity, R.drawable.arrow_back_white_24)
                }
            }
            invalidateOptionsMenu()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (viewModel.user.value?.userId == (application as Eden).userId) {
            return when (collapsingToolBarState) {
                CollapsingToolBarState.COLLAPSED -> {
                    menuInflater.inflate(R.menu.user_toolbar_menu_collapsed, menu)
                    true
                }

                CollapsingToolBarState.IN_BETWEEN -> {
                    false
                }

                CollapsingToolBarState.EXPANDED -> {
                    menuInflater.inflate(R.menu.user_toolbar_menu_expanded, menu)
                    true
                }
            }
        }
        else return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.profileEditButton -> {
                Intent(this@UserProfileActivity, EditProfileActivity::class.java).apply {
                    startActivity(this)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}