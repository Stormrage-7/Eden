package com.example.eden.ui.viewmodels

import androidx.lifecycle.AndroidViewModel
import com.example.eden.Eden
import com.example.eden.database.AppRepository
import timber.log.Timber

class PostInteractionsViewModel(
    repository: AppRepository,
    application: Eden
): AndroidViewModel(application) {

    var upvotedPostList = repository.getUpvotedPosts(application.userId)
    var downvotedPostList = repository.getDownvotedPosts(application.userId)
    var commentList = repository.getCommentsOfUser(application.userId)
    val communityList = repository.getCommunityList(application.userId)
    var userList = repository.getUserList()

    init {
        Timber.tag("PostInteractions").i("PostInteractionsViewModel Initialized!")
    }

}