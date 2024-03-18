package com.example.eden.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eden.database.AppRepository
import com.example.eden.entities.Post
import com.example.eden.entities.ImageUri
import com.example.eden.entities.relations.PostCommunityCrossRef
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

class NewPostViewModel(private val repository: AppRepository,
                       application: Application): AndroidViewModel(application) {

    val _counter = repository.getImgFileCounter()
    var counter = -1
    init {
        Timber.tag("NewPostViewModel").i("NewPostViewModel Initialized!")
    }

    //Edit
    fun upsertPost(post: Post){
        viewModelScope.launch {
            repository.upsertPost(post).toInt()
        }
    }

    //Create
    fun upsertPost(post: Post, communityId: Int): Int{
        viewModelScope.launch {
            val postId1 = async{
                repository.upsertPost(post) }
            repository.insertPostCommunityCrossRef(PostCommunityCrossRef(postId1.await().toInt(), communityId))
            repository.increasePostCount(communityId)
        }
        return 1
    }

    fun upsertPost(post: Post, communityId: Int, imgUri: ImageUri): Int{
        viewModelScope.launch {
            repository.upsertImgUri(imgUri)
            val postId1 = async{
                repository.upsertPost(post) }
            repository.insertPostCommunityCrossRef(PostCommunityCrossRef(postId1.await().toInt(), communityId))
            repository.increasePostCount(communityId)
        }
        return 1
    }

    fun updatePost(postId: Int, bodyText: String) {
        viewModelScope.launch {
            repository.updatePost(postId, bodyText)
        }
    }


}