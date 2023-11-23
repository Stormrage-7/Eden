package com.example.eden

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eden.entities.Post
import kotlinx.coroutines.launch

class NewPostViewModel(private val repository: PostRepository,
    application: Application): AndroidViewModel(application) {

    init {
        Log.i("NewPostViewModel", "NewPostViewModel Initialized!")
    }

    fun upsertPost(post: Post){
        viewModelScope.launch {
            repository.upsertPost(post)
        }
    }

}