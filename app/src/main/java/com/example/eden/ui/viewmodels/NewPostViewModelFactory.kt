package com.example.eden.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eden.database.AppRepository

class NewPostViewModelFactory(
    private val repository: AppRepository,
    private val application: Application): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewPostViewModel::class.java)){
            return NewPostViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class!")
//        return modelClass.getConstructor(AppRepository::class.java).newInstance(repository)
    }
}