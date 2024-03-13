package com.example.eden.util

import androidx.recyclerview.widget.DiffUtil
import com.example.eden.entities.Comment

class CommentDiffUtil(
    private val oldList: List<Comment>,
    private val newList: List<Comment>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].commentId == newList[newItemPosition].commentId
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].commentId != newList[newItemPosition].commentId -> false
            oldList[oldItemPosition].posterName != newList[newItemPosition].posterName -> false
            oldList[oldItemPosition].text != newList[newItemPosition].text -> false
            oldList[oldItemPosition].imageUri != newList[newItemPosition].imageUri -> false
            oldList[oldItemPosition].voteCounter != newList[newItemPosition].voteCounter -> false
            oldList[oldItemPosition].voteStatus != newList[newItemPosition].voteStatus -> false
            oldList[oldItemPosition].postId != newList[newItemPosition].postId -> false
            oldList[oldItemPosition].communityId != newList[newItemPosition].communityId -> false
            else -> true
        }
    }
}