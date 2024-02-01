package com.example.eden.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.eden.ui.viewmodels.CommentsViewModel
import com.example.eden.ui.viewmodels.CommentsViewModelFactory

class NewCommentActivity: AppCompatActivity(),
    ConfirmationDialogFragment.ConfirmationDialogListener{

    private lateinit var activityNewCommentBinding: ActivityNewCommentBinding
    private lateinit var viewModel: CommentsViewModel
    private lateinit var database: AppDatabase
    private lateinit var repository: AppRepository
    private lateinit var factory: CommentsViewModelFactory

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
            viewModel.upsertComment(Comment(0, text = comment, postId = postId, communityId = communityId))
            finish()
        }
    }

    override fun onDialogPositiveClick() {
        finish()
    }

    override fun onDialogNegativeClick() {
    }

    override fun onBackPressed() {
        val discardChangesDialog = ConfirmationDialogFragment("Are you sure you want to discard changes and exit?")
        discardChangesDialog.show(supportFragmentManager, "DiscardChangesDialog")
//        super.onBackPressed()
    }
}