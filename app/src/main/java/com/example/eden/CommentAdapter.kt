package com.example.eden

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.eden.databinding.ItemCommentBinding
import com.example.eden.databinding.ItemPostBinding
import com.example.eden.entities.Community
import com.example.eden.entities.Post
import com.example.eden.entities.relations.PostCommunityCrossRef
import com.example.eden.enums.VoteStatus

class CommentAdapter(
    val context: Context): RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    var commentList: List<Comment> = listOf()
    inner class CommentViewHolder(val binding: ItemCommentBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.binding.apply {
            val comment = commentList[position]
            commentTextView.text = comment.text
        }
    }

    fun updateCommentList(commentList: List<Comment>) {
        this.commentList = commentList
        notifyDataSetChanged()
    }


}