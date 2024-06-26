package com.example.eden.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.eden.R
import com.example.eden.databinding.ItemCommentBinding
import com.example.eden.databinding.ItemDetailedPostBinding
import com.example.eden.databinding.ItemNoContentBinding
import com.example.eden.entities.Community
import com.example.eden.entities.User
import com.example.eden.models.CommentModel
import com.example.eden.models.CommunityModel
import com.example.eden.models.PostModel
import com.example.eden.util.CommentDiffUtil
import com.example.eden.util.UriValidator

const val ITEM_POST_HEADER = 0
const val ITEM_COMMENT = 1
const val ITEM_NO_CONTENT = 2
class PostWithCommentsAdapter(
    private val context: Context,
    private val postListener: PostListener
): RecyclerView.Adapter<ViewHolder>() {

    private var commentList: List<CommentModel> = listOf()
    private var userList: List<User> = listOf()
    var post: PostModel? = null
    var community: CommunityModel? = null
    lateinit var resources: Resources

    inner class NoContentViewHolder(val binding: ItemNoContentBinding): ViewHolder(binding.root){
        fun bind(){
            binding.apply {
                tempTextView.text = "No Comments"
            }
        }
    }

    inner class CommentViewHolder(val binding: ItemCommentBinding): ViewHolder(binding.root){
        init {
            binding.apply {
                likeBtn.setOnClickListener {
                    postListener.commentUpvoteButtonClick( commentList[bindingAdapterPosition-1] )
//                    notifyItemRangeChanged(bindingAdapterPosition, bindingAdapterPosition+1)
//                    notifyItemChanged(bindingAdapterPosition)
                }
                dislikeBtn.setOnClickListener {
                    postListener.commentDownvoteButtonClick( commentList[bindingAdapterPosition-1])
//                    notifyItemRangeChanged(bindingAdapterPosition, bindingAdapterPosition+1)
//                    notifyItemChanged(bindingAdapterPosition)
                }
                imageViewUser.setOnClickListener { postListener.onUserClick(commentList[bindingAdapterPosition-1].posterId) }
                textViewUserName.setOnClickListener { postListener.onUserClick(commentList[bindingAdapterPosition-1].posterId) }
            }
        }
        fun bind(comment: CommentModel){
            val user = userList.find { comment.posterId == it.userId }
            binding.apply {
                if (user != null) {
                    textViewUserName.text = user.username
                    if (!user.isCustomImage) imageViewUser.setImageResource(user.profileImageUri.toInt())
                    else {
                        if (UriValidator.validate(context, user.profileImageUri)) imageViewUser.setImageURI(
                            Uri.parse(user.profileImageUri))
                        else imageViewUser.setImageResource(user.profileImageUri.toInt())
                    }
                }
                if (comment.commentText.isEmpty()) commentTextView.visibility = View.GONE
                else {
                    commentTextView.visibility = View.VISIBLE
                    commentTextView.text = comment.commentText
                }
                if (UriValidator.validate(context, comment.imageUri)){
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

    inner class PostViewHolder(val binding: ItemDetailedPostBinding): ViewHolder(binding.root){

        init {
            binding.apply {
                // CHANGES TO THE VOTE
                likeBtn.setOnClickListener { postListener.onUpvoteBtnClick() }
                dislikeBtn.setOnClickListener { postListener.onDownvoteBtnClick() }
                shareBtn.setOnClickListener {
                    if (post != null) postListener.onShareClick(post!!.postId, post!!.communityId)
                }
                bookmarkBtn.setOnClickListener { postListener.onBookmarkClick() }
                textViewUserName.setOnClickListener { postListener.onUserClick(post!!.posterId) }
            }
        }
        @SuppressLint("ResourceAsColor")
        fun bind(post: PostModel?, community: CommunityModel?){

            binding.apply {
                //COMMUNITY DETAILS
                if (community != null) {
                    if (community.isCustomImage) {
                        if (UriValidator.validate(
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
                    val user = userList.find { it.userId == post.posterId }
                    user?.let { textViewUserName.text = user.username }

                    textViewTitle.text = post.title

                    //MEDIA
                    if(post.containsImage and post.isCustomImage){
                        if (UriValidator.validate(context, post.imageUri)){
                            imageViewPost.visibility = View.VISIBLE
                            imageViewPost.setImageURI(Uri.parse(post.imageUri))
                        }
                        else imageViewPost.visibility = View.GONE
                    }
                    else if (post.containsImage and !post.isCustomImage) {
                        imageViewPost.visibility = View.VISIBLE
                        imageViewPost.setImageResource(post.imageUri.toInt())
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

                    when(post.isBookmarked){
                        true -> bookmarkBtn.setIconResource(R.drawable.ic_bookmark_filled)
                        false -> bookmarkBtn.setIconResource(R.drawable.ic_bookmark)
                    }
                    likeBtn.setIconResource(post.voteStatus.upvoteIconDrawable)
                    dislikeBtn.setIconResource(post.voteStatus.downvoteIconDrawable)
                    textViewVoteCounter.setTextColor(ContextCompat.getColor(context, post.voteStatus.textViewColor))
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (commentList.isEmpty()){
            if (position == 0) ITEM_POST_HEADER else ITEM_NO_CONTENT
        } else{
            if (position == 0) ITEM_POST_HEADER else ITEM_COMMENT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (getItemViewType(position) == ITEM_POST_HEADER) (holder as PostViewHolder).bind(post, community)
        else if (getItemViewType(position) == ITEM_NO_CONTENT) (holder as NoContentViewHolder).bind()
        else (holder as CommentViewHolder).bind(commentList[position - 1])
    }

    fun updateCommentList(newCommentList: List<CommentModel>) {
        val diffUtil = CommentDiffUtil(commentList, newCommentList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        commentList = newCommentList
        notifyDataSetChanged()
//        commentList = newCommentList
//        notifyDataSetChanged()
    }

    fun updateUserList(newUserList: List<User>) {
        userList = newUserList
    }

    interface PostListener{
        fun onCommunityClick(community: Community)
        fun onUserClick(userId: Int)
        fun onUpvoteBtnClick()
        fun onDownvoteBtnClick()
        fun onBookmarkClick()
        fun commentUpvoteButtonClick(comment: CommentModel)
        fun commentDownvoteButtonClick(comment: CommentModel)
        fun onShareClick(postId: Int, communityId: Int)
    }

}