package com.example.eden.ui.viewmodels

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eden.Eden
import com.example.eden.database.AppRepository
import com.example.eden.enums.VoteStatus
import com.example.eden.models.PostModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookmarksViewModel(private val repository: AppRepository,
                         private val application: Eden
): AndroidViewModel(application) {

    val user = repository.getUser(application.userId)
    val postList = repository.getBookmarkedPostList(application.userId)
    val communityList = repository.getCommunityList(application.userId)
    val userList = repository.getUserList()


    init {
        Log.i("Testing", "BookmarksViewModel Initialized - ${postList.toString()}")
    }


    fun upvotePost(post: PostModel){
        when(post.voteStatus){
            VoteStatus.UPVOTED -> viewModelScope.launch(Dispatchers.IO) { repository.updatePostInteractions(postId = post.postId, userId = application.userId, voteStatus = VoteStatus.NONE, isBookMarked = post.isBookmarked)
                repository.removePostUpvote(post.postId, post.posterId)}
            VoteStatus.DOWNVOTED -> viewModelScope.launch(Dispatchers.IO) { repository.updatePostInteractions(postId = post.postId, userId = application.userId, voteStatus = VoteStatus.UPVOTED, isBookMarked = post.isBookmarked)
                repository.downvoteToUpvotePost(post.postId)}
            VoteStatus.NONE -> viewModelScope.launch(Dispatchers.IO) { repository.updatePostInteractions(postId = post.postId, userId = application.userId, voteStatus = VoteStatus.UPVOTED, isBookMarked = post.isBookmarked)
                repository.upvotePost(post.postId, post.posterId)}
        }
    }

    fun downvotePost(post: PostModel){
        when(post.voteStatus){
            VoteStatus.UPVOTED -> viewModelScope.launch(Dispatchers.IO) { repository.updatePostInteractions(application.userId, post.postId, VoteStatus.DOWNVOTED, post.isBookmarked)
                repository.upvoteToDownvotePost(post.postId)}
            VoteStatus.DOWNVOTED -> viewModelScope.launch(Dispatchers.IO) { repository.updatePostInteractions(application.userId, post.postId, VoteStatus.NONE, post.isBookmarked)
                repository.removePostDownvote(post.postId, post.posterId)}
            VoteStatus.NONE -> viewModelScope.launch(Dispatchers.IO) { repository.updatePostInteractions(application.userId, post.postId, VoteStatus.DOWNVOTED, post.isBookmarked)
                repository.downvotePost(post.postId, post.posterId)}
        }
    }

    fun bookmarkPost(post: PostModel){
        viewModelScope.launch { repository.updatePostInteractions(application.userId, post.postId, post.voteStatus,
            when(post.isBookmarked){
                true -> false
                false -> true
            }) }
    }

}