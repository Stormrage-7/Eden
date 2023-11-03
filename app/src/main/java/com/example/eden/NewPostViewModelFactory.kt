package com.example.eden

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NewPostViewModelFactory(
    private val repository: PostRepository,
    private val application: Application): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewPostViewModel::class.java)){
            return NewPostViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class!")

    }
}