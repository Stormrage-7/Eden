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
    private lateinit var repository: AppRepository
    private lateinit var factory: ViewModelFactory
    private var isImageAttached = false
    private var imageUri = ""
    private var imageUriList = listOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityNewPostBinding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(activityNewPostBinding.root)

        factory = (this.application as Eden).viewModelFactory
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


            viewModel.upsertPost(Post(0, titleText, isImageAttached, imageUri,  bodyText, communityId = 1))
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
            val pickImage = Intent().apply {
                type = "image/*"
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    action = Intent.ACTION_GET_CONTENT
                } else {
                    action = Intent.ACTION_OPEN_DOCUMENT
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    addCategory(Intent.CATEGORY_OPENABLE)

                }
            }
//            resultLauncher.launch(pickImage)
            startActivityForResult(pickImage, 100)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == 100 && data != null){
            var lastUri = ""
            val takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

            if (data?.clipData != null) {
                var count = data.clipData?.itemCount

                for (i in 0 until count!!) {
                    val current = data.clipData!!.getItemAt(i).uri.toString()
                    application.contentResolver.takePersistableUriPermission(Uri.parse(current), takeFlags)
                    imageUri = "$imageUri+$current"
                    lastUri = current
                }

            } else if (data?.data != null) {
                // if single image is selected
                imageUri = data.data.toString()
                lastUri = imageUri
                application.contentResolver.takePersistableUriPermission(Uri.parse(imageUri), takeFlags)
            }

            activityNewPostBinding.imageViewPost.setImageURI(Uri.parse(lastUri))
            activityNewPostBinding.imageViewPost.visibility = View.VISIBLE
            activityNewPostBinding.imageViewPost.scaleType = ImageView.ScaleType.FIT_XY
            isImageAttached = true
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}