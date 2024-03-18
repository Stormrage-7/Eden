package com.example.eden.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.eden.Eden
import com.example.eden.R
import com.example.eden.database.AppDatabase
import com.example.eden.database.AppRepository
import com.example.eden.databinding.ActivityEditProfileBinding
import com.example.eden.dialogs.ConfirmationDialogFragment
import com.example.eden.enums.Countries
import com.example.eden.ui.NewPostActivity.Companion.PICK_IMAGE
import com.example.eden.ui.viewmodels.ProfileViewModel
import com.example.eden.ui.viewmodels.ProfileViewModelFactory
import com.example.eden.util.DateUtils
import com.example.eden.util.FileGenerationResponse
import com.example.eden.util.ImageGenerator
import com.example.eden.util.ProfileValidator
import com.example.eden.util.SafeClickListener
import com.example.eden.util.UriValidator
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import timber.log.Timber
import java.util.Calendar
import java.util.Date
import java.util.TimeZone


class EditProfileActivity : AppCompatActivity(), ConfirmationDialogFragment.ConfirmationDialogListener {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var database: AppDatabase
    private lateinit var repository: AppRepository
    private lateinit var factory: ProfileViewModelFactory
    private lateinit var dob: Date
    private var imageUri = ""
    private var isImageAttached = false

    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var contactNo: String
    private lateinit var country: Countries
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(applicationContext as Eden)
        repository = AppRepository(database.edenDao())
        factory = ProfileViewModelFactory(repository, (application as Eden).userId ,application as Eden)
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        viewModel._counter.observe(this) {
            if (it != null) viewModel.counter = it
        }

        setSupportActionBar(binding.editProfileToolbar)
        binding.editProfileToolbar.title = "Edit Profile"
        binding.editProfileToolbar.setNavigationOnClickListener {
            if (changesMade()) {
                val discardChangesDialog = ConfirmationDialogFragment("Are you sure you want to discard any changes made and exit?")
                discardChangesDialog.show(supportFragmentManager, "DiscardChangesDialog")
            }
            else finish()
        }

        viewModel.user.observe(this) { user ->
            binding.apply {
                if (user.isCustomImage){
                    if(UriValidator.validate(this@EditProfileActivity, user.profileImageUri)){
                        imageViewProfile.setImageURI(
                            Uri.parse(user.profileImageUri))
                        deleteImageBtn.visibility = View.VISIBLE
                        isImageAttached = true
                        imageUri = user.profileImageUri
                    }
                    else{
                        imageViewProfile.setImageResource(R.drawable.ic_avatar)
                        deleteImageBtn.visibility = View.INVISIBLE
                        isImageAttached = false
                        imageUri = R.drawable.ic_avatar.toString()
                    }
                }
                else {
                    if (user.userId in 1..3) {
                        imageViewProfile.setImageResource(user.profileImageUri.toInt())
                        isImageAttached = true
                        imageUri = user.profileImageUri
                    }
                    else {
                        imageViewProfile.setImageResource(R.drawable.ic_avatar)
                        isImageAttached = false
                        imageUri = R.drawable.ic_avatar.toString()
                    }
                }


                firstName = user.firstName
                lastName = user.lastName
                email = user.email
                contactNo = user.mobileNo
                country = user.country
                dob = user.dob

                firstNameEditText.setText(firstName)
                lastNameEditText.setText(lastName)
                emailEditText.setText(email)
                mobileNoEditText.setText(contactNo)
                dobEditText.setText(DateUtils.toSimpleString(dob))
                countryDropDown2.setText(country.text, false)
            }
        }

        binding.apply {

            deleteImageBtn.setOnClickListener {
                imageUri = R.drawable.ic_avatar.toString()
                imageViewProfile.setImageResource(imageUri.toInt())
                isImageAttached = false
                deleteImageBtn.visibility = View.INVISIBLE
            }

            //Image
            uploadImageButton.setOnClickListener {
                val pickImage = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_OPEN_DOCUMENT
                    addCategory(Intent.CATEGORY_OPENABLE)
                }
                startActivityForResult(pickImage, PICK_IMAGE)
            }

            //First Name
            firstNameEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(firstName: Editable) {
                    if (firstName.toString().trim().isNotEmpty() and ProfileValidator.validateName(firstName.toString().trim())) firstNameEditText.error = null
                    else firstNameEditText.error = "Invalid First Name"
                }
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            })

            //Last Name
            lastNameEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(lastName: Editable) {
                    if (lastName.toString().trim().isNotEmpty() and ProfileValidator.validateName(lastName.toString().trim())) lastNameEditText.error = null
                    else lastNameEditText.error = "Invalid Last Name"
                }
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            })

            //Email
            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()

            emailEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {

                    if (s.toString().trim().isNotEmpty() and s.toString().trim().matches(emailPattern)) {
                        emailEditText.error = null
                    } else {
                        emailEditText.error = "Invalid Email"
                    }
                }
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            })

            //Mobile
            mobileNoEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(mobileNo: Editable) {
                    if (mobileNo.toString().trim().isNotEmpty() and ProfileValidator.validateMobileNo(mobileNo.toString().trim()) ) mobileNoEditText.error = null
                    else mobileNoEditText.error = "Invalid Phone/Mobile Number"
                }
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            })

            //DOB
            dobEditText.setSafeOnClickListener {
                val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                val today = MaterialDatePicker.todayInUtcMilliseconds()
                cal.timeInMillis = today

                val datePicker =
                    MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select DOB")
                        .setSelection(dob.time)
                        .setCalendarConstraints(
                            CalendarConstraints.Builder()
                                .setEnd(cal.timeInMillis)
                                .setOpenAt(cal.timeInMillis)
                                .setValidator(DateValidatorPointBackward.now())
                                .build()
                        )
                        .build()

                datePicker.addOnPositiveButtonClickListener {
                    dob = Date(it)
                    dobEditText.setText(DateUtils.toSimpleString(Date(it)))
                }
                datePicker.show(supportFragmentManager, "DATE_PICKER")
            }

            // Country Field
            val countries = Countries.values().map { it.text }.toTypedArray()
            countryDropDown2.setSimpleItems(countries)

            submitButtonEditProfile.setOnClickListener {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this@EditProfileActivity)
                    builder
                        .setMessage("Are you sure you want to update your profile with the given details?")
                        .setPositiveButton("Yes") { _, _ ->
                            updateProfile()
                        }
                        .setNegativeButton("No") { _, _ ->
                        }

                    val confirmationDialog: AlertDialog = builder.create()
                    confirmationDialog.show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == NewPostActivity.PICK_IMAGE && data != null) {
            val takeFlags =
                (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

            if (data.data != null) {
                // if single image is selected
                imageUri = data.data.toString()
                application.contentResolver.takePersistableUriPermission(
                    Uri.parse(imageUri),
                    takeFlags
                )
            }

            Log.i("IMAGE URI", imageUri)

            if (UriValidator.validate(this, imageUri)){
                binding.imageViewProfile.setImageURI(Uri.parse(imageUri))
                binding.imageViewProfile.visibility = View.VISIBLE
                binding.imageViewProfile.scaleType = ImageView.ScaleType.CENTER_CROP
                isImageAttached = true
                binding.deleteImageBtn.visibility = View.VISIBLE
            }
            else{
                imageUri = ""
                isImageAttached = false
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDialogPositiveClick() {
        finish()
    }

    override fun onDialogNegativeClick() {

    }

    override fun onBackPressed() {
         if (changesMade()) {
            val discardChangesDialog = ConfirmationDialogFragment("Are you sure you want to discard changes and exit?")
            discardChangesDialog.show(supportFragmentManager, "DiscardChangesDialog")
        }
        else super.onBackPressed()
    }

    private fun updateProfile(){
        if(binding.firstNameEditText.error == null &&
            binding.lastNameEditText.error == null &&
            binding.mobileNoEditText.error == null &&
            binding.emailEditText.error == null){
            val firstName = binding.firstNameEditText.text.toString()
            val lastName = binding.lastNameEditText.text.toString()
            val contactNo = binding.mobileNoEditText.text.toString()
            val country = Countries.valueOf(binding.countryDropDown2.text.toString().replace(" ", "_"))
            val email = binding.emailEditText.text.toString()
            Log.i("SUBMIT BUTTON", "$country, $imageUri, $isImageAttached")

            // IMAGE Uri Validation
            if (isImageAttached) {
                if (UriValidator.validate(this, imageUri)) {
                    when (val fileGenerationResponse = ImageGenerator.generate(this, imageUri, viewModel.counter)){
                        FileGenerationResponse.Error -> {
                            Timber.tag("FileGenerationResponse").i("Error")
                            return
                        }
                        is FileGenerationResponse.Success -> viewModel.updateProfileAndImgUri(firstName, lastName,
                            country, contactNo, email,
                            dob, isImageAttached, fileGenerationResponse.uri.toString())
                    }
                    finish()
                } else {
                    Toast.makeText(this, "Uploaded image not found!", Toast.LENGTH_LONG).show()
                    isImageAttached = false
                    imageUri = R.drawable.ic_avatar.toString()
                    binding.imageViewProfile.setImageResource(imageUri.toInt())
                    return
                }
            } else {
                viewModel.updateProfile(firstName, lastName, country, contactNo, email, dob, isImageAttached, imageUri)
                finish()
            }
        }
    }

    private fun View.setSafeOnClickListener(onSafeCLick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeCLick(it)
        }
        setOnClickListener(safeClickListener)
    }

    private fun changesMade(): Boolean {
        return firstName != binding.firstNameEditText.text.toString() ||
            lastName != binding.lastNameEditText.text.toString() ||
            country != Countries.valueOf(binding.countryDropDown2.text.toString().replace(" ", "_")) ||
            contactNo != binding.mobileNoEditText.text.toString() ||
            email != binding.emailEditText.text.toString() ||
            dob != viewModel.user.value?.dob ||
            imageUri != viewModel.user.value?.profileImageUri
    }
}