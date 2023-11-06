package com.example.eden

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: PostRepository,
                    application: Application): AndroidViewModel(application) {

//    private var _postList = repository.getAllPosts()
//
//    val postList: LiveData<MutableList<Post>>
//        get() = _postList

    val postList = repository.postList

    init {
        Log.i("Testing", "HomeViewModel Initialized - ${postList.toString()}")
        refreshPostListFromRepository()

    }

    private fun refreshPostListFromRepository(){
        viewModelScope.launch {
            repository.refreshPosts()
            Log.i("Refresh Method", "Refreshed!")
        }
    }

}