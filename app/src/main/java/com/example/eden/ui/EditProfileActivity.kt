package com.example.eden.ui

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.eden.Eden
import com.example.eden.database.AppDatabase
import com.example.eden.database.AppRepository
import com.example.eden.databinding.ActivityEditProfileBinding
import com.example.eden.ui.viewmodels.ProfileViewModel
import com.example.eden.ui.viewmodels.ProfileViewModelFactory
import com.example.eden.util.DateUtils
import com.example.eden.util.UriValidation
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Date

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var database: AppDatabase
    private lateinit var repository: AppRepository
    private lateinit var factory: ProfileViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(applicationContext as Eden)
        repository = AppRepository(database.edenDao())
        factory = ProfileViewModelFactory(repository, application as Eden)
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        setSupportActionBar(binding.editProfileToolbar)
        binding.editProfileToolbar.title = "Edit Profile"
        binding.editProfileToolbar.setNavigationOnClickListener { finish() }

        viewModel.user.observe(this) { user ->
            binding.apply {
                firstNameEditText.setText(user.firstName)
                lastNameEditText.setText(user.lastName)
                emailEditText.setText(user.email)
                mobileNoEditText.setText(user.mobileNo)
                dobEditText.setText(DateUtils.toSimpleString(user.dob))
            }
        }

        binding.apply {
            dobEditText.setOnClickListener {
                val datePicker =
                    MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select DOB")
                        .setSelection(viewModel.user.value?.dob?.time)
                        .build()

                datePicker.addOnPositiveButtonClickListener {
                    dobEditText.setText(DateUtils.toSimpleString(Date(it)))
                }
                datePicker.show(supportFragmentManager, "DATE_PICKER")
            }
        }
    }

}