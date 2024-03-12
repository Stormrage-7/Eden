package com.example.eden.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.eden.R
import com.example.eden.databinding.ItemCommentBinding
import com.example.eden.entities.Comment
import com.example.eden.ui.SearchableActivity
import com.example.eden.util.CommentDiffUtil
import com.example.eden.util.UriValidator
import kotlin.math.abs

class CommentAdapter(
    private val context: Context, private val commentClickListener: CommentClickListener): RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private var commentList: List<Comment> = listOf()
    inner class CommentViewHolder(val binding: ItemCommentBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.binding.apply {
            val comment = commentList[position]
            textViewUserName.text = comment.posterName
            commentTextView.text = comment.text
            if (UriValidator.validate(context, comment.imageUri)) {
                imageViewComment.visibility = View.VISIBLE
                imageViewComment.setImageURI(Uri.parse(comment.imageUri))
                imageViewComment.scaleType = ImageView.ScaleType.CENTER_CROP
            }
            textViewVoteCounter.text = comment.voteCounter.toString()

            if (context is SearchableActivity) {
                holder.binding.apply {
                    if (comment.voteCounter<0){
                        textViewVoteCounter.text = abs(comment.voteCounter).toString()
                        textView.text = "Downvotes"
                    }
                    else textView.text = "Upvotes"

                    textViewVoteCounter.setTextColor(ContextCompat.getColor(context, R.color.black))
                    likeBtn.visibility = View.GONE
                    dislikeBtn.visibility = View.GONE
                    textView.visibility = View.VISIBLE
                }
            }
        }
        holder.itemView.setOnClickListener {
            commentClickListener.onCommentClick(commentList[position])
        }
    }

    fun updateCommentList(newCommentList: List<Comment>) {
        val diffUtil = CommentDiffUtil(commentList, newCommentList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        commentList = newCommentList
        diffResults.dispatchUpdatesTo(this)
    }

    interface CommentClickListener{
        fun onCommentClick(comment: Comment)
    }
}