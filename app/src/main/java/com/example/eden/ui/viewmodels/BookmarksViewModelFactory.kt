package com.example.eden.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eden.Eden
import com.example.eden.database.AppRepository

class BookmarksViewModelFactory(
    private val repository: AppRepository,
    private val application: Eden
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookmarksViewModel::class.java)){
            return BookmarksViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class!")

    }
}