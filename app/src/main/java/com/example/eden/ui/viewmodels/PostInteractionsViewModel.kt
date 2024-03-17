package com.example.eden.ui.viewmodels

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eden.Eden
import com.example.eden.database.AppRepository
import com.example.eden.models.PostModel
import kotlinx.coroutines.launch
import timber.log.Timber

class PostInteractionsViewModel(
    private val repository: AppRepository,
    private val application: Eden
): AndroidViewModel(application) {

    var upvotedPostList = repository.getUpvotedPosts(application.userId)
    var downvotedPostList = repository.getDownvotedPosts(application.userId)
    var commentList = repository.getCommentsOfUser(application.userId)
    val communityList = repository.getCommunityList(application.userId)
    var userList = repository.getUserList()

    init {
        Timber.tag("PostInteractions").i("PostInteractionsViewModel Initialized!")
    }

    fun bookmarkPost(post: PostModel){
        viewModelScope.launch {
            repository.updatePostInteractions(application.userId, post.postId, post.voteStatus,
                when(post.isBookmarked) {
                    true -> false
                    false -> true
                })
        }
    }

}