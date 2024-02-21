package com.example.eden.ui.viewmodels

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eden.database.AppRepository
import com.example.eden.Eden
import com.example.eden.entities.Comment
import com.example.eden.entities.Post
import com.example.eden.enums.VoteStatus
import kotlinx.coroutines.launch
import timber.log.Timber

class DetailedPostViewModel(private val repository: AppRepository,
                            postId: Int,
                            communityId: Int,
                            application: Eden
): AndroidViewModel(application) {

    val post = repository.getPostWithId(postId)
    val commentList = repository.getCommentListForPost(postId)
    val community = repository.getCommunity(communityId)
    val user = repository.getUser()

    init {
        Timber.tag("Testing").i("DetailedPostViewModel Initialized")
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

    fun deletePost(){
        viewModelScope.launch {
            repository.deletePost(post.value!!)
            repository.decreasePostCount(community.value!!.communityId)
        }
    }

    fun upvoteComment(comment: Comment){
        val temp: Comment = when(comment.voteStatus){
            VoteStatus.UPVOTED -> comment.copy(voteCounter = comment.voteCounter-1, voteStatus = VoteStatus.NONE)
            VoteStatus.DOWNVOTED -> comment.copy(voteCounter = comment.voteCounter+2, voteStatus = VoteStatus.UPVOTED)
            VoteStatus.NONE -> comment.copy(voteCounter = comment.voteCounter+1, voteStatus = VoteStatus.UPVOTED)
        }
        viewModelScope.launch {
            repository.upsertComment(temp)
        }
    }
    fun downvoteComment(comment: Comment){
        val temp: Comment = when(comment.voteStatus){
            VoteStatus.UPVOTED -> comment.copy(voteCounter = comment.voteCounter-2, voteStatus = VoteStatus.DOWNVOTED)
            VoteStatus.DOWNVOTED -> comment.copy(voteCounter = comment.voteCounter+1, voteStatus = VoteStatus.NONE)
            VoteStatus.NONE -> comment.copy(voteCounter = comment.voteCounter-1, voteStatus = VoteStatus.DOWNVOTED)
        }
        viewModelScope.launch {
            repository.upsertComment(temp)
        }
    }

//    fun getCommunityForPost(communityId: Int){
//        viewModelScope.launch {
//            community = repository.getCommunity(communityId)
//        }
//    }

}