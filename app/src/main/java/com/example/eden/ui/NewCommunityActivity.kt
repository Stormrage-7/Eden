package com.example.eden.ui

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.eden.database.AppDatabase
import com.example.eden.database.AppRepository
import com.example.eden.ui.viewmodels.CommunitiesViewModel
import com.example.eden.ui.viewmodels.CommunityViewModelFactory
import com.example.eden.Eden
import com.example.eden.R
import com.example.eden.databinding.ActivityNewCommunityBinding
import com.example.eden.entities.Community


class NewCommunityActivity: AppCompatActivity()  {
    private lateinit var activityNewCommunityBinding: ActivityNewCommunityBinding
    private lateinit var viewModel: CommunitiesViewModel
    private lateinit var database: AppDatabase
    private lateinit var repository: AppRepository
    private lateinit var factory: CommunityViewModelFactory
    private var isImageAttached = false
    private var imageUri = R.drawable.icon_logo.toString()
    private var communityNameList = mutableListOf<String>()

    private val PICK_IMAGE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityNewCommunityBinding = ActivityNewCommunityBinding.inflate(layoutInflater)


        database = AppDatabase.getDatabase(application as Eden)
        repository = AppRepository(database.edenDao())
        factory = CommunityViewModelFactory(repository, application as Eden)
        viewModel = ViewModelProvider(this, factory)[CommunitiesViewModel::class.java]

        viewModel.communityList.observe(this, Observer {
            viewModel.updateCommunityNameList(it)
            setContentView(activityNewCommunityBinding.root)
        })

        Log.i("NEW COMMUNITY out function", viewModel.communityNameList.toString())


        //CLOSE BUTTON
        activityNewCommunityBinding.closeBtn.setOnClickListener {
            finish()
        }

        activityNewCommunityBinding.nextButton.apply {
            isEnabled = false
        }

        //REMOVE IMAGE BUTTON
        activityNewCommunityBinding.deleteImageBtn.visibility = View.GONE
        activityNewCommunityBinding.deleteImageBtn.setOnClickListener {
            isImageAttached = false
            imageUri = R.drawable.icon_logo.toString()
            activityNewCommunityBinding.imageViewCommunity.setImageResource(imageUri.toInt())
            activityNewCommunityBinding.deleteImageBtn.visibility = View.GONE
            activityNewCommunityBinding.tempTextView.visibility = View.VISIBLE
        }

        //COMMUNITY NAME EDIT-TEXT
        activityNewCommunityBinding.communityNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("Not yet implemented")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.trim()?.isNotEmpty() == true) {
                    activityNewCommunityBinding.nextButton.isEnabled = true
                    activityNewCommunityBinding.nextButton.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(resources, R.color.azure, null)
                    )
                } else {
                    activityNewCommunityBinding.nextButton.isEnabled = false
                    activityNewCommunityBinding.nextButton.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(resources, R.color.grey, null)
                    )
                }
            }

            override fun afterTextChanged(p0: Editable?) {
//                TODO("Not yet implemented")
            }

        })

        //COMMUNITY CREATE BUTTON
        activityNewCommunityBinding.nextButton.setOnClickListener {
            val communityName = activityNewCommunityBinding.communityNameEditText.text.toString()
            val communityDescription = activityNewCommunityBinding.communityDescriptionEditText.text.toString()
            if(viewModel.communityNameList.contains(communityName)){
                Toast.makeText(this, "Community already exists! Please try again...", Toast.LENGTH_LONG).show()
            }
            else{
                Log.i("Inside Create Button!", "HELLO!")
                val community = Community(0, communityName = communityName, description = communityDescription, isCustomImage = isImageAttached, imageUri = imageUri)
//                if (isImageAttached) community = Community(0, communityName = communityName, description = "", isCustomImage = isImageAttached, imageUri = imageUri)
//                else community = Community(0, communityName = communityName, description = "", isCustomImage = isImageAttached, imageUri = imageUri)

                viewModel.upsertCommunity(community)
                Toast.makeText(this, "Community has been created!", Toast.LENGTH_LONG).show()
                finish()
            }
        }


        activityNewCommunityBinding.imageViewCommunity.setOnClickListener {
            val pickImage = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_OPEN_DOCUMENT
//                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                addCategory(Intent.CATEGORY_OPENABLE)
            }
//            resultLauncher.launch(pickImage)
            startActivityForResult(pickImage, PICK_IMAGE)
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE && data != null) {
            val takeFlags =
                (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

            if (data.data != null) {
                // if single image is selected
                imageUri = data.data.toString()
//                lastUri = imageUri
                application.contentResolver.takePersistableUriPermission(
                    Uri.parse(imageUri),
                    takeFlags
                )
                Log.i("IMAGE URI before setting", imageUri)
                activityNewCommunityBinding.imageViewCommunity.setImageURI(Uri.parse(imageUri))
                activityNewCommunityBinding.imageViewCommunity.scaleType = ImageView.ScaleType.CENTER_CROP
                activityNewCommunityBinding.deleteImageBtn.visibility = View.VISIBLE
                activityNewCommunityBinding.tempTextView.visibility = View.GONE
                isImageAttached = true
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}