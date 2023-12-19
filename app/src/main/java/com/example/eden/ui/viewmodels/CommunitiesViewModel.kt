package com.example.eden.ui.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eden.database.AppRepository
import com.example.eden.Eden
import com.example.eden.entities.Community
import kotlinx.coroutines.launch

@SuppressLint("LogNotTimber")
class CommunitiesViewModel(private val repository: AppRepository,
                           application: Eden
): AndroidViewModel(application) {


    val communityList = repository.communityList
    var communityNameList = mutableListOf<String>()

    init {
        Log.i("Community List", communityList.value.toString())
        Log.i("Testing", "CommunitiesViewModel Initialized!")
        refreshCommunityListFromRepository()
        Log.i("Community List", repository.communityList.value.toString())
    }

    fun onJoinClick(position: Int) {
        val community = communityList.value!![position]
        val temp: Community = when(community.isJoined){
                true -> {
                    viewModelScope.launch{
                        repository.deleteFromJoinedCommunities(community.communityId)
                    }
                    community.copy(noOfMembers = community.noOfMembers-1, isJoined = false)
                }
                false -> {
                    viewModelScope.launch {
                        repository.insertIntoJoinedCommunities(community.communityId)
                    }
                    community.copy(noOfMembers = community.noOfMembers+1, isJoined = true)
                }
            }
        viewModelScope.launch{
            repository.upsertCommunity(temp)
        }
    }

    fun upsertCommunity(community: Community) {
        viewModelScope.launch {
            repository.upsertCommunity(community)
        }
    }

    private fun refreshCommunityListFromRepository(){
        Log.i("Inside Refresh Community", "Community viewmodel")
        repository.refreshCommunities()
    }

    fun updateCommunityNameList(communityList: List<Community>){
        communityNameList = mutableListOf()
        communityList.forEach { community -> communityNameList.add(community.communityName) }
        Log.i("NEW COMMUNITY in function", communityNameList.toString())
    }

}