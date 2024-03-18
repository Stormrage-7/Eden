package com.example.eden.ui.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eden.database.AppRepository
import com.example.eden.Eden
import com.example.eden.entities.Community
import com.example.eden.entities.ImageUri
import com.example.eden.models.CommunityModel
import kotlinx.coroutines.launch

@SuppressLint("LogNotTimber")
class CommunitiesViewModel(private val repository: AppRepository,
                           private val application: Eden
): AndroidViewModel(application) {

    val _counter = repository.getImgFileCounter()
    var counter = -1
    val communityList = repository.getCommunityList(userId = application.userId)
    var communityNameList = mutableListOf<String>()

    init {
        Log.i("Community List", communityList.value.toString())
        Log.i("Testing", "CommunitiesViewModel Initialized!")
        refreshCommunityListFromRepository()
    }

    fun onJoinClick(position: Int) {
        val community = communityList.value!![position]
        when(community.isJoined){
                true -> viewModelScope.launch{ repository.updateCommunityInteractions(application.userId, community.communityId, false)
                    repository.unJoinCommunity(community.communityId) }
                false -> viewModelScope.launch { repository.updateCommunityInteractions(application.userId, community.communityId, true)
                    repository.joinCommunity(community.communityId) }
        }
    }

    fun upsertCommunity(community: Community) {
        viewModelScope.launch {
            repository.upsertCommunity(community)
        }
    }

    fun upsertCommunity(community: Community, imageUri: ImageUri) {
        viewModelScope.launch {
            repository.upsertImgUri(imageUri)
            repository.upsertCommunity(community)
        }
    }

    private fun refreshCommunityListFromRepository(){
        Log.i("Inside Refresh Community", "Community viewmodel")
    }

    fun updateCommunityNameList(communityList: List<CommunityModel>){
        communityNameList = mutableListOf()
        communityList.forEach { community -> communityNameList.add(community.communityName) }
        Log.i("NEW COMMUNITY in function", communityNameList.toString())
    }

    fun updateCommunity(communityId: Int, communityDescription: String) {
        viewModelScope.launch { repository.updateCommunity(communityId, communityDescription) }
    }

}