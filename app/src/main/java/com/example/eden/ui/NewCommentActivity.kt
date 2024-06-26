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
import androidx.lifecycle.ViewModelProvider
import com.example.eden.Eden
import com.example.eden.R
import com.example.eden.database.AppDatabase
import com.example.eden.database.AppRepository
import com.example.eden.databinding.ActivityNewCommentBinding
import com.example.eden.dialogs.ConfirmationDialogFragment
import com.example.eden.entities.Comment
import com.example.eden.entities.ImageUri
import com.example.eden.ui.NewPostActivity.Companion.PICK_IMAGE
import com.example.eden.ui.viewmodels.CommentsViewModel
import com.example.eden.ui.viewmodels.CommentsViewModelFactory
import com.example.eden.util.FileGenerationResponse
import com.example.eden.util.ImageGenerator
import com.example.eden.util.UriValidator
import timber.log.Timber

class NewCommentActivity: AppCompatActivity(),
    ConfirmationDialogFragment.ConfirmationDialogListener{

    private lateinit var activityNewCommentBinding: ActivityNewCommentBinding
    private lateinit var viewModel: CommentsViewModel
    private lateinit var database: AppDatabase
    private lateinit var repository: AppRepository
    private lateinit var factory: CommentsViewModelFactory
    private var isImageAttached = false
    private var imageUri = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityNewCommentBinding = ActivityNewCommentBinding.inflate(layoutInflater)
        setContentView(activityNewCommentBinding.root)

        database = AppDatabase.getDatabase(applicationContext as Eden)
        repository = AppRepository(database.edenDao())
        factory = CommentsViewModelFactory(repository, application as Eden)
        viewModel = ViewModelProvider(this, factory)[CommentsViewModel::class.java]

        viewModel._counter.observe(this) {
            if (it != null) viewModel.counter =it
        }

        activityNewCommentBinding.nextButton.isEnabled = false
        activityNewCommentBinding.closeBtn.setOnClickListener {
            if (isPageEdited()) {
                val discardChangesDialog =
                    ConfirmationDialogFragment("Are you sure you want to discard changes and exit?")
                discardChangesDialog.show(supportFragmentManager, "DiscardChangesDialog")
            }
            else finish()
        }

        activityNewCommentBinding.commentEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("Not yet implemented")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (isPageEdited()) {
                    activityNewCommentBinding.nextButton.isEnabled = true
                    activityNewCommentBinding.nextButton.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(resources, R.color.purple, null)
                    )
                } else {
                    activityNewCommentBinding.nextButton.isEnabled = false
                    activityNewCommentBinding.nextButton.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(resources, R.color.grey, null)
                    )
                }
            }

            override fun afterTextChanged(p0: Editable?) {
//                TODO("Not yet implemented")
            }

        })

        val postTitle = intent.getStringExtra("PostTitle")!!
        val postId = intent.getIntExtra("PostId",-1)
        val communityId = intent.getIntExtra("CommunityId", -1)
        activityNewCommentBinding.postTitleTextView.text = postTitle
        activityNewCommentBinding.nextButton.setOnClickListener {
            val comment = activityNewCommentBinding.commentEditText.text.toString()
            if (isImageAttached) {
                if (UriValidator.validate(this, imageUri)) {
                    when (val fileGenerationResponse = ImageGenerator.generate(this, imageUri, viewModel.counter)){
                        FileGenerationResponse.Error -> {
                            Timber.tag("FileGenerationResponse").i("Error")
                            return@setOnClickListener
                        }
                        is FileGenerationResponse.Success -> {
                            val community = Comment(0, commentText = comment,
                                voteCounter = (0..25).random(), imageUri = fileGenerationResponse.uri.toString(),
                                postId = postId, postTitle = postTitle, communityId = communityId, posterId = (application as Eden).userId)
                            viewModel.upsertComment(community, ImageUri(0, fileGenerationResponse.uri.toString()))
                        }
                    }
                } else {
                    Toast.makeText(this, "Uploaded image not found!", Toast.LENGTH_LONG).show()
                    isImageAttached = false
                    imageUri = ""
                    activityNewCommentBinding.imageViewComment.visibility = View.GONE
                    activityNewCommentBinding.removeImageButton.visibility = View.GONE
                    return@setOnClickListener
                }
            } else if (isPageEdited()) viewModel.upsertComment(Comment(0, commentText = comment, voteCounter = (0..25).random(), postId = postId, postTitle = postTitle,
                communityId = communityId, posterId = (application as Eden).userId))
            finish()
        }

        activityNewCommentBinding.insertImagesBtn.setOnClickListener {
            val pickImage = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_OPEN_DOCUMENT
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            startActivityForResult(pickImage, PICK_IMAGE)
        }

        activityNewCommentBinding.removeImageButton.setOnClickListener {
            activityNewCommentBinding.imageViewComment.visibility = View.GONE
            isImageAttached = false
            imageUri = ""
            if (!isPageEdited()) activityNewCommentBinding.nextButton.backgroundTintList = ColorStateList.valueOf(
                ResourcesCompat.getColor(resources, R.color.grey, null)
            )
            activityNewCommentBinding.removeImageButton.visibility = View.GONE
        }
    }

    override fun onDialogPositiveClick() {
        finish()
    }

    override fun onDialogNegativeClick() {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE && data != null) {
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
                activityNewCommentBinding.apply {
                    imageViewComment.setImageURI(Uri.parse(imageUri))
                    imageViewComment.visibility = View.VISIBLE
                    imageViewComment.scaleType = ImageView.ScaleType.CENTER_CROP
                    nextButton.isEnabled = true
                    nextButton.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(resources, R.color.azure, null)
                    )
                    removeImageButton.visibility = View.VISIBLE
                }
                isImageAttached = true
            }
            else{
                imageUri = ""
                isImageAttached = false
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (isPageEdited()) showDiscardChangesDialog()
        else super.onBackPressed()
    }

    private fun isPageEdited() = (activityNewCommentBinding.commentEditText.text.toString().trim().isNotEmpty() || isImageAttached)

    private fun showDiscardChangesDialog(){
        val discardChangesDialog = ConfirmationDialogFragment("Are you sure you want to discard changes and exit?")
        discardChangesDialog.show(supportFragmentManager, "DiscardChangesDialog")
    }
}