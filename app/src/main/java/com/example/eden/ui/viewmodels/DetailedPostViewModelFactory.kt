package com.example.eden.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eden.database.AppRepository
import com.example.eden.Eden
import com.example.eden.entities.Community
import com.example.eden.entities.Post

class DetailedPostViewModelFactory(private val repository: AppRepository,
                                   private val postId: Int,
                                   private val communityId: Int,
                                   private val application: Eden
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailedPostViewModel::class.java)){
            return DetailedPostViewModel(repository, postId, communityId, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class!")

    }
}