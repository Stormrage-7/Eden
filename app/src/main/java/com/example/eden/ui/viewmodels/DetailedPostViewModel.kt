package com.example.eden.ui.viewmodels

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eden.database.AppRepository
import com.example.eden.Eden
import com.example.eden.entities.Comment
import com.example.eden.entities.Community
import com.example.eden.entities.Post
import com.example.eden.enums.VoteStatus
import kotlinx.coroutines.launch

class DetailedPostViewModel(private val repository: AppRepository,
                            val post: Post,
                            val community: Community,
                            application: Eden
): AndroidViewModel(application) {

    val commentList = repository.getCommentListForPost(post.postId)

    init {
        Log.i("Testing", "DetailedPostViewModel Initialized")
    }

    fun upvotePost(){

        when(post.voteStatus){
            VoteStatus.UPVOTED -> {
                post.voteCounter -= 1
                post.voteStatus = VoteStatus.NONE
            }
            VoteStatus.DOWNVOTED -> {
                post.voteCounter += 2
                post.voteStatus = VoteStatus.UPVOTED
            }
            VoteStatus.NONE -> {
                post.voteCounter += 1
                post.voteStatus = VoteStatus.UPVOTED
            }
        }

        viewModelScope.launch {
            repository.upsertPost(post)
        }
    }

    fun downvotePost(){

        when(post.voteStatus){
            VoteStatus.UPVOTED -> {
                post.voteCounter -= 2
                post.voteStatus = VoteStatus.DOWNVOTED
            }
            VoteStatus.DOWNVOTED -> {
                post.voteCounter += 1
                post.voteStatus = VoteStatus.NONE
            }
            VoteStatus.NONE -> {
                post.voteCounter -= 1
                post.voteStatus = VoteStatus.DOWNVOTED
            }
        }

        viewModelScope.launch{
            repository.upsertPost(post)
        }
    }

    fun addComment(comment: Comment) {
        viewModelScope.launch {
            repository.upsertComment(comment)
        }
    }

}