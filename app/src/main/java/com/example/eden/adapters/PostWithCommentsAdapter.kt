package com.example.eden.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.eden.R
import com.example.eden.databinding.BottomSheetPostFilterBinding
import com.example.eden.databinding.ItemCommentBinding
import com.example.eden.databinding.ItemDetailedCommunityBinding
import com.example.eden.databinding.ItemDetailedPostBinding
import com.example.eden.databinding.ItemNoContentBinding
import com.example.eden.databinding.ItemPostBinding
import com.example.eden.entities.Comment
import com.example.eden.entities.Community
import com.example.eden.entities.Post
import com.example.eden.enums.PostFilter
import com.example.eden.enums.VoteStatus
import com.example.eden.util.UriValidation
import com.google.android.material.bottomsheet.BottomSheetDialog

const val ITEM_POST_HEADER = 0
const val ITEM_COMMENT = 1
const val ITEM_NO_CONTENT = 2
class PostWithCommentsAdapter(
    val context: Context,
    private val postListener: PostListener
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var commentList: List<Comment> = listOf()
    var post: Post? = null
    var community: Community? = null
    lateinit var resources: Resources

    inner class NoContentViewHolder(val binding: ItemNoContentBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(){
            binding.apply {
                tempTextView.text = "No Comments"
            }
        }
    }

    inner class CommentViewHolder(val binding: ItemCommentBinding): RecyclerView.ViewHolder(binding.root){
        init {
            binding.apply {
                likeBtn.setOnClickListener {
                    postListener.commentUpvoteButtonClick( commentList[bindingAdapterPosition-1] )
//                    notifyItemChanged(bindingAdapterPosition)
                }
                dislikeBtn.setOnClickListener {
                    postListener.commentDownvoteButtonClick( commentList[bindingAdapterPosition-1])
//                    notifyItemChanged(bindingAdapterPosition)
                }
            }
        }
        fun bind(comment: Comment){
            binding.apply {
                textViewUserName.text = comment.posterName
                commentTextView.text = comment.text
                if (UriValidation.validate(context, comment.imageUri)){
                    imageViewComment.visibility = View.VISIBLE
                    imageViewComment.setImageURI(Uri.parse(comment.imageUri))
                    imageViewComment.scaleType = ImageView.ScaleType.CENTER_CROP
                }

                textViewVoteCounter.text = comment.voteCounter.toString()
                likeBtn.setIconResource(comment.voteStatus.upvoteIconDrawable)
                dislikeBtn.setIconResource(comment.voteStatus.downvoteIconDrawable)
                textViewVoteCounter.setTextColor(ContextCompat.getColor(context, comment.voteStatus.textViewColor))
            }
        }
    }

    inner class PostViewHolder(val binding: ItemDetailedPostBinding): RecyclerView.ViewHolder(binding.root){

        init {
            binding.apply {
                // CHANGES TO THE VOTE
                likeBtn.setOnClickListener { postListener.onUpvoteBtnClick() }
                dislikeBtn.setOnClickListener { postListener.onDownvoteBtnClick() }

//                    shareBtn.visibility = View.INVISIBLE
                shareBtn.setOnClickListener {
                    if (post != null) {
                        postListener.onShareClick(post!!.postId, post!!.communityId)
                    }
                }
            }
        }
        @SuppressLint("ResourceAsColor")
        fun bind(post: Post?, community: Community?){
            binding.apply {
                //COMMUNITY DETAILS
                if (community != null) {
                    if (community.isCustomImage) {
                        if (UriValidation.validate(
                                context,
                                community.imageUri
                            )
                        ) imageViewCommunity.setImageURI(Uri.parse(community.imageUri))
                        else imageViewCommunity.setImageResource(R.drawable.icon_logo)
                    }
                    else imageViewCommunity.setImageResource(community.imageUri.toInt())
                    textViewCommunityName.text = community.communityName

                } else {
                    imageViewCommunity.setImageResource(R.drawable.icon_logo)
                    textViewCommunityName.text = ""
                }
                //POST DETAILS
                if (post != null) {
                    textViewTitle.text = post.title

                    //MEDIA
                    if(post.containsImage and UriValidation.validate(context, post.imageUri)){
                        imageViewPost.apply {
                            visibility = View.VISIBLE
                            setImageURI(Uri.parse(post.imageUri))
                            scaleType = ImageView.ScaleType.CENTER_CROP
                        }
                    }
                    else imageViewPost.visibility = View.GONE

                    //BODY TEXT
                    if (post.bodyText.isEmpty()) {
                        textViewDescription.visibility = View.GONE
                    } else {
                        textViewDescription.visibility = View.VISIBLE
                        textViewDescription.text = post.bodyText
                    }

                    //VOTE SYSTEM
                    textViewVoteCounter.text = post.voteCounter.toString()

                    likeBtn.setIconResource(post.voteStatus.upvoteIconDrawable)
                    dislikeBtn.setIconResource(post.voteStatus.downvoteIconDrawable)
                    textViewVoteCounter.setTextColor(ContextCompat.getColor(context, post.voteStatus.textViewColor))
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (commentList.isEmpty()){
            return if (position == 0) ITEM_POST_HEADER else ITEM_NO_CONTENT
        }
        else{
            return if (position==0) ITEM_POST_HEADER else ITEM_COMMENT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType){
            ITEM_POST_HEADER -> {
                val binding = ItemDetailedPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding)
            }
            ITEM_NO_CONTENT -> {
                val binding = ItemNoContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                NoContentViewHolder(binding)
            }
            ITEM_COMMENT -> {
                val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CommentViewHolder(binding)
            }
            else -> {
                val binding = ItemDetailedPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int{
        return if (commentList.isEmpty()){
            2
        } else {
            commentList.size+1
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (getItemViewType(position) == ITEM_POST_HEADER) (holder as PostViewHolder).bind(post, community)
        else if (getItemViewType(position) == ITEM_NO_CONTENT) (holder as NoContentViewHolder).bind()
        else (holder as CommentViewHolder).bind(commentList[position - 1])
    }

    fun updateCommentList(commentList: List<Comment>) {
        this.commentList = commentList
        notifyDataSetChanged()
    }

    interface PostListener{
        fun onCommunityClick(community: Community)
        fun onUpvoteBtnClick()
        fun onDownvoteBtnClick()
        fun commentUpvoteButtonClick(comment: Comment)
        fun commentDownvoteButtonClick(comment: Comment)
        fun onShareClick(postId: Int, communityId: Int)
    }

}