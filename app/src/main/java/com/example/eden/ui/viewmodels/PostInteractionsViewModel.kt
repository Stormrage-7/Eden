package com.example.eden.ui.viewmodels

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eden.Eden
import com.example.eden.database.AppRepository
import com.example.eden.entities.Community
import kotlinx.coroutines.launch

class PostInteractionsViewModel(private val repository: AppRepository,
                                application: Eden
): AndroidViewModel(application) {

    var upvotedPostList = repository.getUpvotedPosts(application.userId)
    var downvotedPostList = repository.getDownvotedPosts(application.userId)
    val communityList = repository.communityList

    init {
        Log.i("PostInteractions", "PostInteractionsViewModel Initialized!")
    }

}