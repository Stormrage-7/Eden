package com.example.eden

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.example.eden.entities.Post
import com.example.eden.databinding.ActivityNewPostBinding

class NewPostActivity: AppCompatActivity()  {
    private lateinit var activityNewPostBinding: ActivityNewPostBinding
    private lateinit var viewModel: NewPostViewModel
    private lateinit var database: AppDatabase
    private lateinit var repository: PostRepository
    private lateinit var factory: NewPostViewModelFactory
    private var isImageAttached = false
    private var imageUri = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityNewPostBinding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(activityNewPostBinding.root)

        database = AppDatabase.getDatabase(this)
        repository = PostRepository(database)
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
                if (p0?.isNotEmpty() == true) {
                    activityNewPostBinding.nextButton.isEnabled = true
                    activityNewPostBinding.nextButton.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(resources, R.color.azure, null)
                    )
                } else {
                    activityNewPostBinding.nextButton.isEnabled = false
                    activityNewPostBinding.nextButton.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(getResources(), R.color.grey, null)
                    )
                }
            }

            override fun afterTextChanged(p0: Editable?) {
//                TODO("Not yet implemented")
            }

        })

        activityNewPostBinding.nextButton.setOnClickListener {
            val titleText = activityNewPostBinding.TitleEditTV.text.toString()
            val bodyText = activityNewPostBinding.bodyTextEditTV.text.toString()

            viewModel.upsertPost(Post(0, titleText, isImageAttached, imageUri,  bodyText))
            Toast.makeText(this, "Post has been Uploaded!", Toast.LENGTH_LONG).show()
            finish()
        }

//        var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                // There are no request codes
//                val data: Intent? = result.data
//            }
//        }

        activityNewPostBinding.insertImagesBtn.setOnClickListener {
            val pickImage = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "image/*"
                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    action = Intent.ACTION_GET_CONTENT;
                } else {
                    action = Intent.ACTION_OPEN_DOCUMENT;
                    addCategory(Intent.CATEGORY_OPENABLE);
                }
            }
//            resultLauncher.launch(pickImage)
            startActivityForResult(pickImage, 100)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == 100 && data != null){
            activityNewPostBinding.imageViewPost.setImageURI(data.data)
            activityNewPostBinding.imageViewPost.visibility = View.VISIBLE
            activityNewPostBinding.imageViewPost.scaleType = ImageView.ScaleType.FIT_XY
            isImageAttached = true
            imageUri = data.data.toString()
            val takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            if (imageUri.isNotEmpty()){
                application.contentResolver.takePersistableUriPermission(Uri.parse(imageUri), takeFlags)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


}