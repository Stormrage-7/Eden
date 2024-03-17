package com.example.eden.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.eden.Eden
import com.example.eden.R
import com.example.eden.adapters.UserProfileViewPagerAdapter
import com.example.eden.database.AppDatabase
import com.example.eden.database.AppRepository
import com.example.eden.databinding.ActivityUserProfileBinding
import com.example.eden.ui.viewmodels.ProfileViewModel
import com.example.eden.ui.viewmodels.ProfileViewModelFactory
import com.example.eden.util.UriValidator
import com.google.android.material.tabs.TabLayoutMediator

class UserProfileActivity: AppCompatActivity() {
    private lateinit var binding: ActivityUserProfileBinding
    lateinit var viewModel: ProfileViewModel
    private lateinit var database: AppDatabase
    private lateinit var repository: AppRepository
    private lateinit var factory: ProfileViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(applicationContext as Eden)
        repository = AppRepository(database.edenDao())
        factory = ProfileViewModelFactory(repository, intent.getIntExtra("UserId", 0), application as Eden)
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        setSupportActionBar(binding.toolbar)

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

        viewModel.user.observe(this) {user ->
            binding.apply {
                if (user.isCustomImage and UriValidator.validate(this@UserProfileActivity, user.profileImageUri)){
                    imageViewProfileHeader.setImageURI(
                        Uri.parse(user.profileImageUri))
//                    imageViewProfileHeader.scaleType = ImageView.ScaleType.CENTER_CROP
                }
                else imageViewProfileHeader.setImageResource(R.drawable.ic_avatar)

                textViewUserNameProfileHeader.text = user.username
                binding.toolbar.title = user.username
                textViewEmailProfileHeader.text = user.email
//                includedLayout.nameTextViewProfile.text = "${user.firstName} ${user.lastName}"
//                includedLayout.emailTextViewProfile.text = user.email
//                includedLayout.mobileTextViewProfile.text = user.mobileNo
//                includedLayout.dobTextViewProfile.text = DateUtils.toSimpleString(user.dob)
//                includedLayout.countryTextViewProfile.text = user.country.text
            }
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.profile_menu, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId){
//            R.id.profileEditButton -> {
//                Intent(this@ProfileActivity, EditProfileActivity::class.java).apply {
//                    startActivity(this)
//                }
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
    }

}