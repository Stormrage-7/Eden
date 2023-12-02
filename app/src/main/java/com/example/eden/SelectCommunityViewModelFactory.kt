package com.example.eden

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

//class SelectCommunityViewModelFactory(
//    private val repository: CommunityRepository,
//    private val application: Application): ViewModelProvider.Factory
//{
//        override fun <T : ViewModel> create(modelClass: Class<T>): T {
//            if (modelClass.isAssignableFrom(CommunitiesViewModel::class.java)) {
//                return CommunitiesViewModel(repository, application) as T
//            }
//            throw IllegalArgumentException("Unknown ViewModel Class!")
//
//        }
//}