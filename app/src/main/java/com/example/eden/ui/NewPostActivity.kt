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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.example.eden.database.AppDatabase
import com.example.eden.database.AppRepository
import com.example.eden.Eden
import com.example.eden.ui.viewmodels.NewPostViewModel
import com.example.eden.ui.viewmodels.NewPostViewModelFactory
import com.example.eden.R
import com.example.eden.entities.Post
import com.example.eden.databinding.ActivityNewPostBinding
import com.example.eden.dialogs.ConfirmationDialogFragment
import com.example.eden.entities.Community
import com.example.eden.entities.ImageUri
import com.example.eden.models.CommunityModel
import com.example.eden.models.PostModel
import com.example.eden.util.FileGenerationResponse
import com.example.eden.util.ImageGenerator
import com.example.eden.util.UriValidator
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneOffset

class NewPostActivity: AppCompatActivity(),
        ConfirmationDialogFragment.ConfirmationDialogListener{
    private lateinit var activityNewPostBinding: ActivityNewPostBinding
    private lateinit var viewModel: NewPostViewModel
    private lateinit var database: AppDatabase
    private lateinit var repository: AppRepository
    private lateinit var factory: NewPostViewModelFactory
    private var isImageAttached = false
    private var imageUri = ""
    private var communityId = -1
    companion object {
        internal const val PICK_IMAGE = 100
        private const val PICK_COMMUNITY = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityNewPostBinding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(activityNewPostBinding.root)

        database = AppDatabase.getDatabase(applicationContext as Eden)
        repository = AppRepository(database.edenDao())
        factory = NewPostViewModelFactory(repository, application)
        viewModel = ViewModelProvider(this, factory)[NewPostViewModel::class.java]

        viewModel._counter.observe(this) {
            if (it != null) viewModel.counter = it
        }

        activityNewPostBinding.nextButton.apply {
            isEnabled = false
        }

        activityNewPostBinding.closeBtn.setOnClickListener {
            if (isPageEdited()) {
                val discardChangesDialog = ConfirmationDialogFragment("Are you sure you want to discard changes and exit?")
                discardChangesDialog.show(supportFragmentManager, "DiscardChangesDialog")
            }
            else finish()
        }

        activityNewPostBinding.TitleEditTV.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.trim()?.isNotEmpty() == true) {
                    activityNewPostBinding.nextButton.isEnabled = true
                    activityNewPostBinding.nextButton.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(resources, R.color.azure, null)
                    )
                } else {
                    activityNewPostBinding.nextButton.isEnabled = false
                    activityNewPostBinding.nextButton.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(resources, R.color.grey, null)
                    )
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        })

        if(intent.hasExtra("Context")){
            if (intent.getStringExtra("Context") == "CommunityDetailedActivity"){
                activityNewPostBinding.apply {
                    communityBar.visibility = View.VISIBLE
                    val community = this@NewPostActivity.intent.getSerializableExtra("CommunityObject") as CommunityModel
                    communityId = community.communityId
                    Log.d("NewPostActivity", communityId.toString())
                    if (community.isCustomImage) imageViewCommunity.setImageURI(Uri.parse(community.imageUri))
                    else imageViewCommunity.setImageResource(Integer.parseInt(community.imageUri))
                    textViewCommunityName.text = community.communityName
                    nextButton.text = "Post"
                }
            }

            if (intent.getStringExtra("Context") == "PostDetailedActivity"){
                activityNewPostBinding.apply {
                    pageTitle.text = "Edit Post"
                    val post = this@NewPostActivity.intent.getSerializableExtra("PostObject") as PostModel
                    val community = this@NewPostActivity.intent.getSerializableExtra("CommunityObject") as CommunityModel
                    communityId = community.communityId
                    TitleEditTV.setText(post.title)
//                    TitleEditTV.visibility = View.GONE
                    TitleEditTV.isEnabled = false
                    insertImagesBtn.visibility = View.GONE
                    bodyTextEditTV.setText(post.bodyText)
                    nextButton.text = "Save"
                }
            }
        }

        activityNewPostBinding.communityBar.setOnClickListener {
            Intent(this@NewPostActivity, SelectCommunityActivity::class.java).apply {
                startActivityForResult(this, PICK_COMMUNITY)
            }
        }

        activityNewPostBinding.nextButton.setOnClickListener {
            if(communityId==-1){
                Log.i("Inside Next Button!", "HELLO!")
                Intent(this, SelectCommunityActivity::class.java).apply {
                    startActivityForResult(this, PICK_COMMUNITY)
                }
            }
            else{
                Log.i("Inside Post Button!", "HELLO!")
                val titleText = activityNewPostBinding.TitleEditTV.text.toString()
                val bodyText = activityNewPostBinding.bodyTextEditTV.text.toString()
                val newPost: Post
                if (intent.hasExtra("Context") and (intent.getStringExtra("Context") == "PostDetailedActivity")) {
                    val post = intent.getSerializableExtra("PostObject") as PostModel
                    viewModel.updatePost(post.postId, bodyText)
                    Toast.makeText(this, "Post has been Updated!", Toast.LENGTH_LONG).show()
                }
                else{
                    if (isImageAttached){
                        if (UriValidator.validate(this@NewPostActivity, imageUri)) {
                            when (val fileGenerationResponse = ImageGenerator.generate(this, imageUri, viewModel.counter)){
                                FileGenerationResponse.Error -> {
                                    Timber.tag("FileGenerationResponse").i("Error")
                                    return@setOnClickListener
                                }
                                is FileGenerationResponse.Success -> {
                                    newPost = Post(0, titleText, isImageAttached, true, fileGenerationResponse.uri.toString(),  bodyText, communityId = communityId, dateTime = LocalDateTime.now().toEpochSecond(
                                        ZoneOffset.UTC), voteCounter = (0..25).random(), posterId = (application as Eden).userId)
                                    viewModel.upsertPost(newPost, communityId, ImageUri(0, fileGenerationResponse.uri.toString()))
                                    Toast.makeText(this, "Post has been Uploaded!", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                        else {
                            Toast.makeText(this, "Uploaded image not found!", Toast.LENGTH_LONG).show()
                            isImageAttached = false
                            imageUri = ""
                            activityNewPostBinding.apply {
                                imageViewPost.visibility = View.GONE
                                removeImageButton.visibility = View.GONE
                            }
                            return@setOnClickListener
                        }
                    } else {
                        newPost = Post(0, titleText, isImageAttached, isCustomImage = false, imageUri,  bodyText, communityId = communityId, dateTime = LocalDateTime.now().toEpochSecond(
                            ZoneOffset.UTC), voteCounter = (0..25).random(), posterId = (application as Eden).userId)
                        viewModel.upsertPost(newPost, communityId)
                        Toast.makeText(this, "Post has been Uploaded!", Toast.LENGTH_LONG).show()
                    }
                }
                finish()
            }
        }

        activityNewPostBinding.removeImageButton.setOnClickListener {
            activityNewPostBinding.apply {
                imageViewPost.visibility = View.GONE
                removeImageButton.visibility = View.GONE
            }
            isImageAttached = false
            imageUri = ""
        }

        activityNewPostBinding.insertImagesBtn.setOnClickListener {
            val pickImage = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_OPEN_DOCUMENT
//                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            startActivityForResult(pickImage, PICK_IMAGE)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE && data != null) {
            var lastUri = ""
            val takeFlags =
                (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

//            if (data.clipData != null) {
//                val count = data.clipData?.itemCount
//
//                for (i in 0 until count!!) {
//                    val current = data.clipData!!.getItemAt(i).uri.toString()
//                    application.contentResolver.takePersistableUriPermission(
//                        Uri.parse(current),
//                        takeFlags
//                    )
//                    imageUri = "$imageUri+$current"
//                    lastUri = current
//                }
//
//            }
            if (data.data != null) {
                // if single image is selected
                imageUri = data.data.toString()
                lastUri = imageUri
                application.contentResolver.takePersistableUriPermission(
                    Uri.parse(imageUri),
                    takeFlags
                )
            }

            Log.i("IMAGE URI", imageUri)

            if (UriValidator.validate(this, imageUri)){
                activityNewPostBinding.apply {
                    imageViewPost.setImageURI(Uri.parse(imageUri))
                    imageViewPost.visibility = View.VISIBLE
//                    imageViewPost.scaleType = ImageView.ScaleType.CENTER_CROP
                    removeImageButton.visibility = View.VISIBLE
                }
                isImageAttached = true
            }
            else{
                imageUri = ""
                isImageAttached = false
            }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_COMMUNITY && data!=null){
            activityNewPostBinding.apply {
                communityBar.visibility = View.VISIBLE
                val community = data.getSerializableExtra("CommunityObject") as CommunityModel
                communityId = community.communityId
                if (community.isCustomImage) {
                    if (UriValidator.validate(this@NewPostActivity, community.imageUri))
                        imageViewCommunity.setImageURI(Uri.parse(community.imageUri))
                    else
                        imageViewCommunity.setImageResource(R.drawable.icon_logo)
                }
                else imageViewCommunity.setImageResource(Integer.parseInt(community.imageUri))
                textViewCommunityName.text = community.communityName
                nextButton.text = "Post"
            }
        }


        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDialogPositiveClick() {
        finish()
    }

    override fun onDialogNegativeClick() {
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (isPageEdited()) {
            val discardChangesDialog = ConfirmationDialogFragment("Are you sure you want to discard changes and exit?")
            discardChangesDialog.show(supportFragmentManager, "DiscardChangesDialog")
        }
        else finish()
//        super.onBackPressed()
    }

    private fun isPageEdited(): Boolean{
        return if (intent.hasExtra("Context") && intent.getStringExtra("Context") == "PostDetailedActivity"){
            val post = intent.getSerializableExtra("PostObject") as PostModel
            (activityNewPostBinding.bodyTextEditTV.text.toString().trim() != post.bodyText)

        } else {
            (activityNewPostBinding.TitleEditTV.text.toString().trim().isNotEmpty() ||
                    activityNewPostBinding.bodyTextEditTV.text.toString().trim()
                        .isNotEmpty() || isImageAttached)
        }

    }

}