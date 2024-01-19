package com.example.eden.adapters

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
import com.example.eden.R
import com.example.eden.databinding.BottomSheetPostFilterBinding
import com.example.eden.databinding.ItemCommentBinding
import com.example.eden.databinding.ItemDetailedCommunityBinding
import com.example.eden.databinding.ItemDetailedPostBinding
import com.example.eden.databinding.ItemPostBinding
import com.example.eden.entities.Comment
import com.example.eden.entities.Community
import com.example.eden.entities.Post
import com.example.eden.enums.PostFilter
import com.example.eden.enums.VoteStatus
import com.google.android.material.bottomsheet.BottomSheetDialog

const val ITEM_POST_HEADER = 0
const val ITEM_COMMENT = 1
class PostWithCommentsAdapter(
    val context: Context,
    private val postListener: PostListener
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var commentList: List<Comment> = listOf()
    lateinit var post: Post
    lateinit var community: Community
    lateinit var resources: Resources

    inner class CommentViewHolder(val binding: ItemCommentBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(comment: Comment){
            binding.apply {
                textViewUserName.text = comment.posterName
                commentTextView.text = comment.text
            }
        }
    }

    inner class PostViewHolder(val binding: ItemDetailedPostBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(post: Post, community: Community){
            binding.apply {
                //COMMUNITY DETAILS
                if (community.isCustomImage) imageViewCommunity.setImageURI(
                    Uri.parse(
                        community.imageUri
                    )
                )
                else imageViewCommunity.setImageResource(community.imageUri.toInt())
                textViewCommunityName.text = community.communityName

                //POST DETAILS
                textViewTitle.text = post.title

                //MEDIA
                if(post.containsImage) {
                    imageViewPost.apply {
                        visibility = View.VISIBLE
                        setImageURI(Uri.parse(post.imageUri))
                        scaleType = ImageView.ScaleType.CENTER_CROP
                    }
                }
                else imageViewPost.visibility = View.GONE

                //BODY TEXT
                if(post.bodyText.isEmpty()){
                    textViewDescription.visibility = View.GONE
                }
                else{
                    textViewDescription.visibility = View.VISIBLE
                    textViewDescription.text = post.bodyText
                }

                //VOTE SYSTEM
                textViewVoteCounter.text = post.voteCounter.toString()

                when (post.voteStatus) {
                    VoteStatus.UPVOTED -> {
                        likeBtn.setImageResource(R.drawable.upvote_circle_up_green_24)
                        dislikeBtn.setImageResource(R.drawable.downvote_circle_down_24)
                        textViewVoteCounter.setTextColor(ContextCompat.getColor(context, R.color.green))
                    }
                    VoteStatus.DOWNVOTED -> {
                        dislikeBtn.setImageResource(R.drawable.downvote_circle_down_red_24)
                        likeBtn.setImageResource(R.drawable.upvote_circle_up_24)
                        textViewVoteCounter.setTextColor(ContextCompat.getColor(context, R.color.red))
                    }
                    VoteStatus.NONE -> {
                        likeBtn.setImageResource(R.drawable.upvote_circle_up_24)
                        dislikeBtn.setImageResource(R.drawable.downvote_circle_down_24)
                        textViewVoteCounter.setTextColor(ContextCompat.getColor(context, R.color.black))
                    }
                }

                // CHANGES TO THE VOTE
                likeBtn.setOnClickListener {
                    Log.i("Like", "Button pressed!")
                    postListener.onUpvoteBtnClick()
                }

                dislikeBtn.setOnClickListener {
                    Log.i("Dislike", "Button pressed!")
                    postListener.onDownvoteBtnClick()
                }

                shareBtn.visibility = View.INVISIBLE
                shareBtn.setOnClickListener {
                    val intent: Intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "HI")
                    }

                    if (intent.resolveActivity(context.packageManager) != null){
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position==0) ITEM_POST_HEADER else ITEM_COMMENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == ITEM_POST_HEADER){
            val binding = ItemDetailedPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            PostViewHolder(binding)
        } else{
            val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            CommentViewHolder(binding)
        }
    }

    override fun getItemCount(): Int {
        return commentList.size+1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (getItemViewType(position) == ITEM_POST_HEADER) (holder as PostViewHolder).bind(post, community)

        else (holder as CommentViewHolder).bind(commentList[position - 1])
    }

    fun updateCommentList(commentList: List<Comment>) {
        this.commentList = commentList
        notifyDataSetChanged()
    }

    private fun updateVoteSystem(binding: ItemDetailedPostBinding) {
        binding.apply {
            textViewVoteCounter.text = post.voteCounter.toString()

            when (post.voteStatus) {
                VoteStatus.UPVOTED -> {
                    likeBtn.setImageResource(R.drawable.upvote_circle_up_green_24)
                    dislikeBtn.setImageResource(R.drawable.downvote_circle_down_24)
                    textViewVoteCounter.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.green
                        )
                    )
                }

                VoteStatus.DOWNVOTED -> {
                    dislikeBtn.setImageResource(R.drawable.downvote_circle_down_red_24)
                    likeBtn.setImageResource(R.drawable.upvote_circle_up_24)
                    textViewVoteCounter.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.red
                        )
                    )
                }

                VoteStatus.NONE -> {
                    likeBtn.setImageResource(R.drawable.upvote_circle_up_24)
                    dislikeBtn.setImageResource(R.drawable.downvote_circle_down_24)
                    textViewVoteCounter.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.black
                        )
                    )
                }
            }
        }
    }

    interface PostListener{
        fun onCommunityClick(community: Community)
        fun onUpvoteBtnClick()
        fun onDownvoteBtnClick()
    }

}