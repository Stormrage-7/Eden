package com.example.eden.util

import androidx.recyclerview.widget.DiffUtil
import com.example.eden.entities.Post

class PostDiffUtil(
    private val oldList: List<Post>,
    private val newList: List<Post>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].postId == newList[newItemPosition].postId
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].postId != newList[newItemPosition].postId -> false
            oldList[oldItemPosition].communityId != newList[newItemPosition].communityId -> false
            oldList[oldItemPosition].title != newList[newItemPosition].title -> false
            oldList[oldItemPosition].bodyText != newList[newItemPosition].bodyText -> false
            oldList[oldItemPosition].containsImage != newList[newItemPosition].containsImage -> false
            oldList[oldItemPosition].imageUri != newList[newItemPosition].imageUri -> false
            oldList[oldItemPosition].voteCounter != newList[newItemPosition].voteCounter -> false
            oldList[oldItemPosition].voteStatus != newList[newItemPosition].voteStatus -> false
            oldList[oldItemPosition].dateTime != newList[newItemPosition].dateTime -> false
            else -> true
        }
    }
}