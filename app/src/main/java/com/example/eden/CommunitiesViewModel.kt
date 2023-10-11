package com.example.eden

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CommunitiesViewModel: ViewModel() {

    // Encapsulation of LiveData by using a Backing Property
    private val _count = MutableLiveData<Int>()
    val count: LiveData<Int>
        get() = _count

    init {
        Log.i("Testing", "CommunitiesViewModel Initialized!")
        _count.value = Community.count
    }

    fun increaseCount(){
        _count.value = (count.value)?.plus(1)
    }

    override fun onCleared() {
        Community.count = count.value ?: 0
    }
}