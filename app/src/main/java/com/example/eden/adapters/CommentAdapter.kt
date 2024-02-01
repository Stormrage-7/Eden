package com.example.eden.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eden.databinding.ItemCommentBinding
import com.example.eden.entities.Comment
import com.example.eden.ui.PostDetailedActivity
import com.example.eden.ui.SearchableActivity

class CommentAdapter(
    val context: Context, private val commentClickListener: CommentClickListener): RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private var commentList: List<Comment> = listOf()
    inner class CommentViewHolder(val binding: ItemCommentBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = CommentViewHolder(binding)
        return CommentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.binding.apply {
            val comment = commentList[position]
            textViewUserName.text = comment.posterName
            commentTextView.text = comment.text
        }
        if (context is SearchableActivity) {
            holder.itemView.setOnClickListener {
                commentClickListener.onCommentClick(commentList[position])
            }
        }
    }

    fun updateCommentList(commentList: List<Comment>) {
        this.commentList = commentList
        notifyDataSetChanged()
    }

    interface CommentClickListener{
        fun onCommentClick(comment: Comment)
    }
}