package com.example.eden

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eden.entities.Community

class CommunitiesViewModel: ViewModel() {

    // Encapsulation of LiveData by using a Backing Property

    private var _communityList = MutableLiveData<List<Community>>()
    val communityList : LiveData<List<Community>>
        get() = _communityList

    init {
        Log.i("Testing", "CommunitiesViewModel Initialized!")
        _communityList.value = Community.communityList
    }

//    override fun onCleared() {
//    }

    fun setCommunityList(communityList: List<Community>) {
        _communityList.value = communityList
    }

    fun onJoinClick(position: Int) {
        _communityList.value!![position].isJoined = !_communityList.value!![position].isJoined
    }
}