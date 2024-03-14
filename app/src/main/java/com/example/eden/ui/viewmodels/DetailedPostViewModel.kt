package com.example.eden.ui.viewmodels

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eden.database.AppRepository
import com.example.eden.Eden
import com.example.eden.entities.Comment
import com.example.eden.entities.Post
import com.example.eden.enums.VoteStatus
import com.example.eden.models.PostModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class DetailedPostViewModel(private val repository: AppRepository,
                            private val postId: Int,
                            communityId: Int,
                            private val application: Eden
): AndroidViewModel(application) {

    val post = repository.getPostWithId(postId, application.userId)
    val commentList = repository.getCommentListForPost(postId)
    val community = repository.getCommunity(communityId)
    val user = repository.getUser(application.userId)

    init {
        Timber.tag("Testing").i("DetailedPostViewModel Initialized")
    }

    fun upvotePost(){
//        val post = postList.value!![position]
        when(post.value?.voteStatus){
            VoteStatus.UPVOTED -> viewModelScope.launch(Dispatchers.IO) { repository.updatePostInteractions(postId = postId, userId = application.userId, voteStatus = VoteStatus.NONE)
                repository.removePostUpvote(postId, application.userId)}
            VoteStatus.DOWNVOTED -> viewModelScope.launch(Dispatchers.IO) { repository.updatePostInteractions(postId = postId, userId = application.userId, voteStatus = VoteStatus.UPVOTED)
                repository.downvoteToUpvotePost(postId)}
            VoteStatus.NONE -> viewModelScope.launch(Dispatchers.IO) { repository.updatePostInteractions(postId = postId, userId = application.userId, voteStatus = VoteStatus.UPVOTED)
                repository.upvotePost(postId, application.userId)}
            else -> {}
        }
    }

    fun downvotePost(){
//        val post = postList.value!![position]
        when(post.value?.voteStatus){
            VoteStatus.UPVOTED -> viewModelScope.launch(Dispatchers.IO) { repository.updatePostInteractions(application.userId, postId, VoteStatus.DOWNVOTED)
                repository.upvoteToDownvotePost(postId)}
            VoteStatus.DOWNVOTED -> viewModelScope.launch(Dispatchers.IO) { repository.updatePostInteractions(application.userId, postId, VoteStatus.NONE)
                repository.removePostDownvote(postId, application.userId)}
            VoteStatus.NONE -> viewModelScope.launch(Dispatchers.IO) { repository.updatePostInteractions(application.userId, postId, VoteStatus.DOWNVOTED)
                repository.downvotePost(postId, application.userId)}
            else -> {}
        }
    }

    fun deletePost(){
        viewModelScope.launch {
            repository.deletePost(post.value!!.postId)
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