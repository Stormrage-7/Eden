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
import com.example.eden.entities.User
import com.example.eden.models.CommentModel
import com.example.eden.ui.PostInteractionsActivity
import com.example.eden.ui.SearchableActivity
import com.example.eden.ui.UserProfileActivity
import com.example.eden.util.CommentDiffUtil
import com.example.eden.util.UriValidator
import kotlin.math.abs

class CommentAdapter(
    private val context: Context, private val commentClickListener: CommentClickListener): RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private var commentList: List<CommentModel> = listOf()
    private var userList: List<User> = listOf()
    inner class CommentViewHolder(val binding: ItemCommentBinding): RecyclerView.ViewHolder(binding.root){
        init {
            binding.imageViewUser.setOnClickListener { commentClickListener.onUserClick(commentList[bindingAdapterPosition].posterId) }
            binding.imageViewUser.setOnClickListener { commentClickListener.onUserClick(commentList[bindingAdapterPosition].posterId) }
            itemView.setOnClickListener { commentClickListener.onCommentClick(commentList[bindingAdapterPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentList[position]
        val user = userList.find { comment.posterId == it.userId }

        holder.binding.apply {
            if (context !is UserProfileActivity && user != null) {
                textViewUserName.text = user.username
                if (!user.isCustomImage) imageViewUser.setImageResource(user.profileImageUri.toInt())
                else {
                    if (UriValidator.validate(
                            context,
                            user.profileImageUri
                        )
                    ) imageViewUser.setImageURI(
                        Uri.parse(user.profileImageUri)
                    )
                    else imageViewUser.setImageResource(user.profileImageUri.toInt())
                }
            }
            else{
                postTitleTextView.visibility = View.VISIBLE
                postTitleTextView.text = comment.postTitle
                imageViewUser.visibility = View.GONE
                textViewUserName.visibility = View.GONE
            }

            commentTextView.text = comment.commentText
            if (UriValidator.validate(context, comment.imageUri)) {
                imageViewComment.visibility = View.VISIBLE
                imageViewComment.setImageURI(Uri.parse(comment.imageUri))
                imageViewComment.scaleType = ImageView.ScaleType.CENTER_CROP
            }
            textViewVoteCounter.text = comment.voteCounter.toString()

            if ((context is SearchableActivity) or (context is PostInteractionsActivity)) {
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
    }

    fun updateCommentList(newCommentList: List<CommentModel>) {
        val diffUtil = CommentDiffUtil(commentList, newCommentList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        commentList = newCommentList
        diffResults.dispatchUpdatesTo(this)
    }

    fun updateUserList(newUserList: List<User>){
        userList = newUserList
    }

    interface CommentClickListener{
        fun onCommentClick(comment: CommentModel)
        fun onUserClick(userId: Int)
    }
}