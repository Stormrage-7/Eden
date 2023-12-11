package com.example.eden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eden.entities.Community
import com.example.eden.entities.Post

class DetailedCommunityViewModelFactory(private val repository: AppRepository,
                                        private val community: Community,
                                        private val application: Eden): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailedCommunityViewModel::class.java)){
            return DetailedCommunityViewModel(repository, community, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class!")

    }
}