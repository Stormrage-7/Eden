package com.example.eden.ui.viewmodels

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eden.database.AppRepository
import com.example.eden.Eden
import com.example.eden.entities.Comment
import com.example.eden.entities.Post
import com.example.eden.enums.VoteStatus
import com.example.eden.models.CommentModel
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
    val commentList = repository.getCommentsForPost(postId, application.userId)
    val community = repository.getCommunity(communityId, application.userId)
    val userList = repository.getUserList()

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

    fun upvoteComment(comment: CommentModel){
        when(comment.voteStatus){
            VoteStatus.UPVOTED -> viewModelScope.launch(Dispatchers.IO) { repository.updateCommentInteractions(commentId = comment.commentId, userId = application.userId, voteStatus = VoteStatus.NONE)
                repository.removeCommentUpvote(comment.commentId)}
            VoteStatus.DOWNVOTED -> viewModelScope.launch(Dispatchers.IO) { repository.updateCommentInteractions(commentId = comment.commentId, userId = application.userId, voteStatus = VoteStatus.UPVOTED)
                repository.downvoteToUpvoteComment(comment.commentId)}
            VoteStatus.NONE -> viewModelScope.launch(Dispatchers.IO) { repository.updateCommentInteractions(commentId = comment.commentId, userId = application.userId, voteStatus = VoteStatus.UPVOTED)
                repository.upvoteComment(comment.commentId)}
        }
    }
    fun downvoteComment(comment: CommentModel){
        when(comment.voteStatus){
            VoteStatus.UPVOTED -> viewModelScope.launch(Dispatchers.IO) { repository.updateCommentInteractions(application.userId, comment.commentId, VoteStatus.DOWNVOTED)
                repository.upvoteToDownvoteComment(comment.commentId)}
            VoteStatus.DOWNVOTED -> viewModelScope.launch(Dispatchers.IO) { repository.updateCommentInteractions(application.userId, comment.commentId, VoteStatus.NONE)
                repository.removeCommentDownvote(comment.commentId)}
            VoteStatus.NONE -> viewModelScope.launch(Dispatchers.IO) { repository.updateCommentInteractions(application.userId, comment.commentId, VoteStatus.DOWNVOTED)
                repository.downvoteComment(comment.commentId)}
        }
    }

//    fun getCommunityForPost(communityId: Int){
//        viewModelScope.launch {
//            community = repository.getCommunity(communityId)
//        }
//    }

}