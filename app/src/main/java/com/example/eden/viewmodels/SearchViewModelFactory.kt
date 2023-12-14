package com.example.eden.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eden.database.AppRepository
import com.example.eden.Eden

class SearchViewModelFactory(
    private val repository: AppRepository,
    private val searchQuery: String,
    private val application: Eden
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(repository, searchQuery, application) as T

//        throw IllegalArgumentException("Unknown ViewModel Class!")
//        return modelClass.getConstructor(AppRepository::class.java).newInstance(repository)
    }
}