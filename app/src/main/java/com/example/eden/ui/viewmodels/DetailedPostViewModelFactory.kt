package com.example.eden.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eden.database.AppRepository
import com.example.eden.Eden
import com.example.eden.entities.Community
import com.example.eden.entities.Post

class DetailedPostViewModelFactory(private val repository: AppRepository,
                                   private val post: Post,
                                   private val community: Community,
                                   private val application: Eden
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailedPostViewModel::class.java)){
            return DetailedPostViewModel(repository, post, community, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class!")

    }
}