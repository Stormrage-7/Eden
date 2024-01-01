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
import com.example.eden.database.AppDatabase
import com.example.eden.database.AppRepository
import com.example.eden.Eden
import com.example.eden.ui.viewmodels.NewPostViewModel
import com.example.eden.ui.viewmodels.NewPostViewModelFactory
import com.example.eden.R
import com.example.eden.entities.Post
import com.example.eden.databinding.ActivityNewPostBinding
import com.example.eden.entities.Community
import com.example.eden.entities.relations.PostCommunityCrossRef

class NewPostActivity: AppCompatActivity()  {
    private lateinit var activityNewPostBinding: ActivityNewPostBinding
    private lateinit var viewModel: NewPostViewModel
    private lateinit var database: AppDatabase
    private lateinit var repository: AppRepository
    private lateinit var factory: NewPostViewModelFactory
    private var isImageAttached = false
    private var imageUri = ""
    private var communityId = -1
    companion object {
        private const val PICK_IMAGE = 100
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


        activityNewPostBinding.nextButton.apply {
            isEnabled = false
        }

        activityNewPostBinding.closeBtn.setOnClickListener {
            finish()
        }

        activityNewPostBinding.TitleEditTV.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("Not yet implemented")
            }

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

            override fun afterTextChanged(p0: Editable?) {
//                TODO("Not yet implemented")
            }

        })

        if(intent.hasExtra("Context")){
            if (intent.getStringExtra("Context") == "CommunityDetailedActivity"){
                activityNewPostBinding.apply {
                    communityBar.visibility = View.VISIBLE
                    val community = this@NewPostActivity.intent.getSerializableExtra("CommunityObject") as Community
                    communityId = community.communityId
                    Log.d("NewPostActivity", communityId.toString())
                    if (community.isCustomImage) imageViewCommunity.setImageURI(Uri.parse(community.imageUri))
                    else imageViewCommunity.setImageResource(Integer.parseInt(community.imageUri))
                    textViewCommunityName.text = community.communityName
                    nextButton.text = "Post"
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
                Log.i("Inside Join Button!", "HELLO!")
                val titleText = activityNewPostBinding.TitleEditTV.text.toString()
                val bodyText = activityNewPostBinding.bodyTextEditTV.text.toString()


                viewModel.upsertPost(Post(0, titleText, isImageAttached, imageUri,  bodyText, communityId = communityId))
                (application as Eden).postId+=1
                viewModel.insertPostCommunityCrossRef(PostCommunityCrossRef((application as Eden).postId, communityId))
                Toast.makeText(this, "Post has been Uploaded!", Toast.LENGTH_LONG).show()
                finish()
            }
        }


        activityNewPostBinding.insertImagesBtn.setOnClickListener {
            val pickImage = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_OPEN_DOCUMENT
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
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

            if (data.clipData != null) {
                val count = data.clipData?.itemCount

                for (i in 0 until count!!) {
                    val current = data.clipData!!.getItemAt(i).uri.toString()
                    application.contentResolver.takePersistableUriPermission(
                        Uri.parse(current),
                        takeFlags
                    )
                    imageUri = "$imageUri+$current"
                    lastUri = current
                }

            } else if (data.data != null) {
                // if single image is selected
                imageUri = data.data.toString()
                lastUri = imageUri
                application.contentResolver.takePersistableUriPermission(
                    Uri.parse(imageUri),
                    takeFlags
                )
            }

            Log.i("IMAGE URI", lastUri)
            activityNewPostBinding.imageViewPost.setImageURI(Uri.parse(lastUri))
            activityNewPostBinding.imageViewPost.visibility = View.VISIBLE
            activityNewPostBinding.imageViewPost.scaleType = ImageView.ScaleType.FIT_XY
            isImageAttached = true
        }
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_COMMUNITY && data!=null){
            activityNewPostBinding.apply {
                communityBar.visibility = View.VISIBLE
                val community = data.getSerializableExtra("CommunityObject") as Community
                communityId = community.communityId
                Log.d("NewPostActivity", communityId.toString())
                if (community.isCustomImage) imageViewCommunity.setImageURI(Uri.parse(community.imageUri))
                else imageViewCommunity.setImageResource(Integer.parseInt(community.imageUri))
                textViewCommunityName.text = community.communityName
                nextButton.text = "Post"
            }
        }


        super.onActivityResult(requestCode, resultCode, data)
    }



}