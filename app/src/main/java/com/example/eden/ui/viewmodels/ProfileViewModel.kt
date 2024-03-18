package com.example.eden.ui.viewmodels

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eden.Eden
import com.example.eden.database.AppRepository
import com.example.eden.entities.ImageUri
import com.example.eden.enums.Countries
import com.example.eden.enums.VoteStatus
import com.example.eden.models.CommentModel
import com.example.eden.models.PostModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class ProfileViewModel(
    private val repository: AppRepository,
    userId: Int,
    private val application: Eden
): AndroidViewModel(application) {

    val _counter = repository.getImgFileCounter()
    var counter = -1
    var user = repository.getUser(userId)
    val postList = repository.getPostsOfUser(application.userId, userId)
    val communityList = repository.getCommunityListForPostsOfUser(userId)
    val commentList = repository.getCommentsOfUser(application.userId, userId)
    init {
        Log.i("Profile", "ProfileViewModel Initialized!")
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
        viewModelScope.launch {
            repository.updatePostInteractions(application.userId, post.postId, post.voteStatus,
                when(post.isBookmarked) {
                    true -> false
                    false -> true
                })
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

    fun updateProfile(firstName: String, lastName: String, country: Countries, contactNo: String, email: String, dob: Date,
                      isCustomImage: Boolean, imageUri: String){
        val temp = user.value!!.copy(firstName = firstName, lastName = lastName, email = email, mobileNo = contactNo, dob = dob,
            country = country, isCustomImage = isCustomImage, profileImageUri = imageUri)
        viewModelScope.launch {
            repository.upsertUser(temp)
        }
    }

    fun updateProfileAndImgUri(firstName: String, lastName: String, country: Countries, contactNo: String, email: String, dob: Date,
                      isCustomImage: Boolean, imageUri: String){
        val temp = user.value!!.copy(firstName = firstName, lastName = lastName, email = email, mobileNo = contactNo, dob = dob,
            country = country, isCustomImage = isCustomImage, profileImageUri = imageUri)
        viewModelScope.launch {
            repository.upsertImgUri(ImageUri(0, imageUri))
            repository.upsertUser(temp)
        }
    }
}