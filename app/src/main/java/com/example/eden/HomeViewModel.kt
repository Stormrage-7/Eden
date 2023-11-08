package com.example.eden

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eden.Enums.VoteStatus
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: PostRepository,
                    application: Application): AndroidViewModel(application) {

    val postList = repository.postList

    init {
        Log.i("Testing", "HomeViewModel Initialized - ${postList.toString()}")
        refreshPostListFromRepository()
    }

    fun upvotePost(position: Int){
        val post = postList.value!![position]
        val temp = Post(post.postId, post.title, post.containsImage, post.imageUri, post.bodyText, voteCounter = post.voteCounter, voteStatus = post.voteStatus)
        when(temp.voteStatus){
            VoteStatus.UPVOTED -> {
                temp.voteCounter-=1
                temp.voteStatus = VoteStatus.NONE
            }
            VoteStatus.DOWNVOTED -> {
                temp.voteCounter+=2
                temp.voteStatus = VoteStatus.UPVOTED
            }
            VoteStatus.NONE -> {
                temp.voteCounter+=1
                temp.voteStatus = VoteStatus.UPVOTED
            }
        }

        viewModelScope.launch {
            repository.upsertPost(temp)
        }
    }

    fun downvotePost(position: Int){
        val post = postList.value!![position]
        val temp = Post(post.postId, post.title, post.containsImage, post.imageUri, post.bodyText, voteCounter = post.voteCounter, voteStatus = post.voteStatus)
        Log.i("Downvote Post", "${post} , ${post}")
        when(temp.voteStatus){
            VoteStatus.UPVOTED -> {
                temp.voteCounter-=2
                temp.voteStatus = VoteStatus.DOWNVOTED
            }
            VoteStatus.DOWNVOTED -> {
                temp.voteCounter+=1
                temp.voteStatus = VoteStatus.NONE
            }
            VoteStatus.NONE -> {
                temp.voteCounter-=1
                temp.voteStatus = VoteStatus.DOWNVOTED
            }
        }

        viewModelScope.launch{
            repository.upsertPost(temp)
        }
    }

    private fun refreshPostListFromRepository(){
        viewModelScope.launch {
            repository.refreshPosts()
            Log.i("Refresh Method", "Refreshed!")
        }
    }

}