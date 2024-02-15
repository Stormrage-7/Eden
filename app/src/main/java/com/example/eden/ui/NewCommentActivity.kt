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
import com.example.eden.ui.NewPostActivity.Companion.PICK_IMAGE
import com.example.eden.ui.viewmodels.CommentsViewModel
import com.example.eden.ui.viewmodels.CommentsViewModelFactory
import com.example.eden.util.UriValidation

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

        activityNewCommentBinding.nextButton.isEnabled = false
        activityNewCommentBinding.closeBtn.setOnClickListener {
            val discardChangesDialog = ConfirmationDialogFragment("Are you sure you want to discard changes and exit?")
            discardChangesDialog.show(supportFragmentManager, "DiscardChangesDialog")
        }

        activityNewCommentBinding.commentEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("Not yet implemented")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.trim()?.isNotEmpty() == true) {
                    activityNewCommentBinding.nextButton.isEnabled = true
                    activityNewCommentBinding.nextButton.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(resources, R.color.azure, null)
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

        val postTitle = intent.getStringExtra("PostTitle")
        val postId = intent.getIntExtra("PostId",-1)
        val communityId = intent.getIntExtra("CommunityId", -1)
        activityNewCommentBinding.postTitleTextView.text = postTitle
        activityNewCommentBinding.nextButton.setOnClickListener {
            val comment = activityNewCommentBinding.commentEditText.text.toString()
            if (isImageAttached) {
                if (UriValidation.validate(this, imageUri)) {
                    viewModel.upsertComment(
                        Comment(
                            0,
                            text = comment,
                            voteCounter = (0..25).random(),
                            imageUri = imageUri,
                            postId = postId,
                            communityId = communityId
                        )
                    )
                } else {
                    Toast.makeText(this, "Uploaded image not found!", Toast.LENGTH_LONG).show()
                    isImageAttached = false
                    imageUri = ""
                    activityNewCommentBinding.imageViewComment.visibility = View.GONE
                    return@setOnClickListener
                }
            } else viewModel.upsertComment(Comment(0, text = comment, voteCounter = (0..25).random(), postId = postId, communityId = communityId))

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

            if (UriValidation.validate(this, imageUri)){
                activityNewCommentBinding.imageViewComment.setImageURI(Uri.parse(imageUri))
                activityNewCommentBinding.imageViewComment.visibility = View.VISIBLE
                activityNewCommentBinding.imageViewComment.scaleType = ImageView.ScaleType.CENTER_CROP
                isImageAttached = true
            }
            else{
                imageUri = ""
                isImageAttached = false
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        val discardChangesDialog = ConfirmationDialogFragment("Are you sure you want to discard changes and exit?")
        discardChangesDialog.show(supportFragmentManager, "DiscardChangesDialog")
//        super.onBackPressed()
    }
}