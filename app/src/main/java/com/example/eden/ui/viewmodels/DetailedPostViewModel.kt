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
                            postObj: Post,
                            communityObj: Community,
                            application: Eden
): AndroidViewModel(application) {

    val post = repository.getPostWithId(postObj.postId)
    val community = repository.getCommunity(communityObj.communityId)
    val commentList = repository.getCommentListForPost(postObj.postId)

    init {
        Log.i("Testing", "DetailedPostViewModel Initialized")
    }

    fun upvotePost(){
        val postValue = post.value!!
        val temp: Post = when(postValue.voteStatus){
            VoteStatus.UPVOTED -> postValue.copy(voteCounter = postValue.voteCounter-1, voteStatus = VoteStatus.NONE)
            VoteStatus.DOWNVOTED -> postValue.copy(voteCounter = postValue.voteCounter+2, voteStatus = VoteStatus.UPVOTED)
            VoteStatus.NONE -> postValue.copy(voteCounter = postValue.voteCounter+1, voteStatus = VoteStatus.UPVOTED)
        }
        viewModelScope.launch {
            repository.upsertPost(temp)
        }
    }

    fun downvotePost(){
        val postValue = post.value!!
        val temp: Post = when(postValue.voteStatus){
            VoteStatus.UPVOTED -> postValue.copy(voteCounter = postValue.voteCounter-2, voteStatus = VoteStatus.DOWNVOTED)
            VoteStatus.DOWNVOTED -> postValue.copy(voteCounter = postValue.voteCounter+1, voteStatus = VoteStatus.NONE)
            VoteStatus.NONE -> postValue.copy(voteCounter = postValue.voteCounter-1, voteStatus = VoteStatus.DOWNVOTED)
        }

        viewModelScope.launch{
            repository.upsertPost(temp)
        }
    }

    fun addComment(comment: Comment) {
        viewModelScope.launch {
            repository.upsertComment(comment)
        }
    }

    fun deletePost(){
        viewModelScope.launch {
            repository.deletePost(post.value!!)
        }
    }

}