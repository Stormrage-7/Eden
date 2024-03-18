package com.example.eden.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eden.database.AppRepository
import com.example.eden.Eden
import com.example.eden.entities.Community
import com.example.eden.models.CommunityModel

class DetailedCommunityViewModelFactory(private val repository: AppRepository,
                                        private val community: CommunityModel,
                                        private val application: Eden
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailedCommunityViewModel::class.java)){
            return DetailedCommunityViewModel(repository, community.communityId, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class!")

    }
}