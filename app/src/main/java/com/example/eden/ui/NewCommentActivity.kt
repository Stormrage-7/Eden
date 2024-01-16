package com.example.eden.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.eden.Eden
import com.example.eden.R
import com.example.eden.database.AppDatabase
import com.example.eden.database.AppRepository
import com.example.eden.databinding.ActivityNewCommentBinding
import com.example.eden.databinding.ActivityNewPostBinding
import com.example.eden.dialogs.DiscardChangesDialogFragment
import com.example.eden.entities.Comment
import com.example.eden.ui.viewmodels.CommentsViewModel
import com.example.eden.ui.viewmodels.CommentsViewModelFactory
import com.example.eden.ui.viewmodels.NewPostViewModel
import com.example.eden.ui.viewmodels.NewPostViewModelFactory

class NewCommentActivity: AppCompatActivity(),
    DiscardChangesDialogFragment.DiscardChangesDialogListener{

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
            val discardChangesDialog = DiscardChangesDialogFragment()
            discardChangesDialog.show(supportFragmentManager, "DiscardChangesDialogFragment")
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
        val postId = intent.getIntExtra("PostId",0)
        activityNewCommentBinding.postTitleTextView.text = postTitle
        activityNewCommentBinding.nextButton.setOnClickListener {
            val comment = activityNewCommentBinding.commentEditText.text.toString()
            viewModel.upsertComment(Comment(0, text = comment, postId = postId))
            finish()
        }
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        finish()
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
    }

    override fun onBackPressed() {
        val discardChangesDialog = DiscardChangesDialogFragment()
        discardChangesDialog.show(supportFragmentManager, "DiscardChangesDialogFragment")
//        super.onBackPressed()
    }
}