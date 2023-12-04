package com.example.eden

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eden.entities.Community
import kotlinx.coroutines.launch

class CommunitiesViewModel(private val repository: AppRepository,
    application: Eden): AndroidViewModel(application) {


    val communityList = repository.communityList

    init {
        Log.i("Community List", "${communityList.value.toString()}")
        Log.i("Testing", "CommunitiesViewModel Initialized!")
        refreshCommunityListFromRepository()
        Log.i("Community List", "${repository.communityList.value.toString()}")
    }

//    override fun onCleared() {
//    }

//    fun setCommunityList(communityList: List<Community>) {
//        _communityList.value = communityList
//    }

    fun onJoinClick(position: Int) {
//        _communityList.value!![position].isJoined = !_communityList.value!![position].isJoined
        val community = communityList.value!![position]
        val temp: Community = when(community.isJoined){
                true -> community.copy(isJoined = false)
                false -> community.copy(isJoined = true)
            }
        viewModelScope.launch{
            repository.upsertCommunity(temp)
        }

    }

    private fun refreshCommunityListFromRepository(){
        viewModelScope.launch {
            repository.refreshCommunities()
        }
    }
}