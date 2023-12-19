package com.example.eden.ui.viewmodels

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eden.database.AppRepository
import com.example.eden.Eden

class HomeViewModelFactory(
    private val repository: AppRepository,
    private val application: Activity): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel(repository, application.application as Eden) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class!")

    }
}