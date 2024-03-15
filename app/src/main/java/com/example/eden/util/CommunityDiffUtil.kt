package com.example.eden.util

import androidx.recyclerview.widget.DiffUtil
import com.example.eden.entities.Community
import com.example.eden.models.CommunityModel

class CommunityDiffUtil(
    private val oldList: List<CommunityModel>,
    private val newList: List<CommunityModel>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].communityId == newList[newItemPosition].communityId
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].communityId != newList[newItemPosition].communityId -> false
            oldList[oldItemPosition].communityName != newList[newItemPosition].communityName -> false
            oldList[oldItemPosition].description != newList[newItemPosition].description -> false
            oldList[oldItemPosition].noOfMembers != newList[newItemPosition].noOfMembers -> false
            oldList[oldItemPosition].noOfPosts != newList[newItemPosition].noOfPosts -> false
            oldList[oldItemPosition].isCustomImage != newList[newItemPosition].isCustomImage -> false
            oldList[oldItemPosition].imageUri != newList[newItemPosition].imageUri -> false
            oldList[oldItemPosition].isJoined != newList[newItemPosition].isJoined -> false
            else -> true
        }
    }
}