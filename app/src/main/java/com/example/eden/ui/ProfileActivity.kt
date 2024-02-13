package com.example.eden.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.eden.Eden
import com.example.eden.R
import com.example.eden.database.AppDatabase
import com.example.eden.database.AppRepository
import com.example.eden.databinding.ActivityProfileBinding
import com.example.eden.ui.viewmodels.CommentsViewModel
import com.example.eden.ui.viewmodels.CommentsViewModelFactory
import com.example.eden.ui.viewmodels.ProfileViewModel
import com.example.eden.ui.viewmodels.ProfileViewModelFactory
import com.example.eden.util.DateUtils
import com.example.eden.util.UriValidation

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var database: AppDatabase
    private lateinit var repository: AppRepository
    private lateinit var factory: ProfileViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(applicationContext as Eden)
        repository = AppRepository(database.edenDao())
        factory = ProfileViewModelFactory(repository, application as Eden)
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        binding.backToHomeTextView.setOnClickListener {
            finish()
        }

        viewModel.user.observe(this) {user ->
            binding.apply {
                if (user.isCustomImage and UriValidation.validate(this@ProfileActivity, user.profileImageUri)){
                    imageViewProfileHeader.setImageURI(
                        Uri.parse(user.profileImageUri))
                }
                else imageViewProfileHeader.setImageResource(user.profileImageUri.toInt())

                textViewUserNameProfileHeader.text = user.username
                textViewEmailProfileHeader.text = user.email
                includedLayout.nameTextViewProfile.text = "${user.firstName} ${user.lastName}"
                includedLayout.emailTextViewProfile.text = user.email
                includedLayout.mobileTextViewProfile.text = user.mobileNo
                includedLayout.dobTextViewProfile.text = DateUtils.toSimpleString(user.dob)
                includedLayout.countryTextViewProfile.text = user.country
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_profile_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.profileEditButton -> {
                Intent(this@ProfileActivity, EditProfileActivity::class.java).apply {
                    startActivity(this)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
    }

}