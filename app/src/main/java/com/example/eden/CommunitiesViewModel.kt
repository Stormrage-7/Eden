package com.example.eden

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CommunitiesViewModel: ViewModel() {
    val count = MutableLiveData<Int>()

    init {
        Log.i("Testing", "CommunitiesViewModel Initialized!")
        count.value = Community.count
    }

    fun increaseCount(){
        count.value = (count.value)?.plus(1)
    }

    override fun onCleared() {
        Community.count = count.value ?: 0
    }
}