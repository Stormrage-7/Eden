package com.example.eden.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eden.database.AppRepository
import com.example.eden.Eden

class SelectCommunityViewModelFactory(
    private val repository: AppRepository,
    private val application: Eden
): ViewModelProvider.Factory
{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CommunitiesViewModel::class.java)) {
                return CommunitiesViewModel(repository, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel Class!")

        }
}