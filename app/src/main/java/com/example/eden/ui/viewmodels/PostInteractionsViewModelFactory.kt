package com.example.eden.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eden.Eden
import com.example.eden.database.AppRepository

class PostInteractionsViewModelFactory(
    private val repository: AppRepository,
    private val application: Eden
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PostInteractionsViewModel(repository, application) as T
    }
}