package com.example.eden

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
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
import com.example.eden.databinding.ActivityNewCommunityBinding
import com.example.eden.entities.Community
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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
        setContentView(activityNewCommunityBinding.root)

        database = AppDatabase.getDatabase(application as Eden)
        repository = AppRepository(database.edenDao())
        factory = CommunityViewModelFactory(repository, application as Eden)
        viewModel = ViewModelProvider(this, factory)[CommunitiesViewModel::class.java]

        viewModel.communityList.observe(this, Observer {
            viewModel.updateCommunityNameList(it)
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
            activityNewCommunityBinding.imageViewPost.setImageResource(imageUri.toInt())
            activityNewCommunityBinding.deleteImageBtn.visibility = View.GONE
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
                        ResourcesCompat.getColor(getResources(), R.color.grey, null)
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
            if(viewModel.communityNameList.contains(communityName)){
                Toast.makeText(this, "Community already exists! Please try again...", Toast.LENGTH_LONG).show()
            }
            else{
                Log.i("Inside Create Button!", "HELLO!")
                val community = Community(0, communityName = communityName, description = "", isCustomImage = isImageAttached, imageUri = imageUri)
//                if (isImageAttached) community = Community(0, communityName = communityName, description = "", isCustomImage = isImageAttached, imageUri = imageUri)
//                else community = Community(0, communityName = communityName, description = "", isCustomImage = isImageAttached, imageUri = imageUri)

                viewModel.upsertCommunity(community)
                Toast.makeText(this, "Post has been Uploaded!", Toast.LENGTH_LONG).show()
                finish()
            }
        }


        activityNewCommunityBinding.imageViewPost.setOnClickListener {
            val pickImage = Intent().apply {
                type = "image/*"
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    action = Intent.ACTION_GET_CONTENT
                } else {
                    action = Intent.ACTION_OPEN_DOCUMENT
//                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    addCategory(Intent.CATEGORY_OPENABLE)

                }
            }
//            resultLauncher.launch(pickImage)
            startActivityForResult(pickImage, PICK_IMAGE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE && data != null) {
            val takeFlags =
                (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

            if (data?.data != null) {
                // if single image is selected
                imageUri = data.data.toString()
//                lastUri = imageUri
                application.contentResolver.takePersistableUriPermission(
                    Uri.parse(imageUri),
                    takeFlags
                )
                Log.i("IMAGE URI before setting", imageUri)
                activityNewCommunityBinding.imageViewPost.setImageURI(Uri.parse(imageUri))
                activityNewCommunityBinding.imageViewPost.scaleType = ImageView.ScaleType.FIT_XY
                activityNewCommunityBinding.deleteImageBtn.visibility = View.VISIBLE
                isImageAttached = true
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}