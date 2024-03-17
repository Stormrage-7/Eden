package com.example.eden.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eden.Eden
import com.example.eden.database.AppRepository

class ProfileViewModelFactory(
    private val repository: AppRepository,
    private val userId: Int,
    private val application: Eden
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(repository, userId, application) as T
    }
}