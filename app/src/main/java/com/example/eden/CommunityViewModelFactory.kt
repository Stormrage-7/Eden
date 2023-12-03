package com.example.eden

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CommunityViewModelFactory(
private val repository: AppRepository,
private val application: Eden): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommunitiesViewModel::class.java)){
            return CommunitiesViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class!")

    }
}