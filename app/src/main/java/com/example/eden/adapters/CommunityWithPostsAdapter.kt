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
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.eden.R
import com.example.eden.databinding.ItemDetailedCommunityBinding
import com.example.eden.databinding.ItemPostBinding
import com.example.eden.entities.Community
import com.example.eden.entities.Post
import com.example.eden.enums.PostFilter
import com.example.eden.util.UriValidator
import com.google.android.material.bottomsheet.BottomSheetDialog

private const val ITEM_COMMUNITY_HEADER = 0
private const val ITEM_POST = 1
class CommunityWithPostsAdapter(
    val context: Context,
    private val postListener: PostListener
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var postList: MutableList<Post> = mutableListOf()
    private var filter = PostFilter.HOT
    lateinit var currentCommunity: Community
    private lateinit var bottomSheetDialog: BottomSheetDialog
    lateinit var resources: Resources

    inner class CommunityViewHolder(val binding: ItemDetailedCommunityBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(community: Community){
            binding.apply {
                if (community.isCustomImage) {
                    if (UriValidator.validate(context, community.imageUri)) imageViewCommunity.setImageURI(Uri.parse(community.imageUri))
                    else imageViewCommunity.setImageResource(R.drawable.icon_logo)
                }
                else imageViewCommunity.setImageResource(community.imageUri.toInt())

                textViewCommunityName.text = community.communityName
                textViewCommunityDescription.text = community.description

                if (community.isJoined){
                    joinButton.text = "Joined"
                    joinButton.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(resources, R.color.grey, null)
                    )
                }
                else{
                    joinButton.text = "Join"
                    joinButton.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(resources, R.color.azure, null)
                    )
                }

                createPostButton.setOnClickListener { postListener.onCreateClick() }
                joinButton.setOnClickListener { postListener.onJoinClick()
//                    updateJoinStatus(binding)
                }

                filterButton.setOnClickListener {

                }
            }
        }
    }

    inner class PostViewHolder(val binding: ItemPostBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(post: Post){
            binding.apply {
                communityBar.visibility = View.GONE
                textViewTitle.text = post.title

                //MEDIA
                if(post.containsImage and UriValidator.validate(context, post.imageUri)){
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

                textViewVoteCounter.text = post.voteCounter.toString()

//                when (post.voteStatus) {
//                    VoteStatus.UPVOTED -> {
//                        likeBtn.setImageResource(R.drawable.upvote_circle_up_green_24)
//                        dislikeBtn.setImageResource(R.drawable.downvote_circle_down_24)
//                        textViewVoteCounter.setTextColor(ContextCompat.getColor(context, R.color.green))
//
//                    }
//                    VoteStatus.DOWNVOTED -> {
//                        dislikeBtn.setImageResource(R.drawable.downvote_circle_down_red_24)
//                        likeBtn.setImageResource(R.drawable.upvote_circle_up_24)
//                        textViewVoteCounter.setTextColor(ContextCompat.getColor(context, R.color.red))
//                    }
//                    VoteStatus.NONE -> {
//                        likeBtn.setImageResource(R.drawable.upvote_circle_up_24)
//                        dislikeBtn.setImageResource(R.drawable.downvote_circle_down_24)
//                        textViewVoteCounter.setTextColor(ContextCompat.getColor(context, R.color.black))
//                    }
//                }

                // CHANGES TO THE VOTE
                likeBtn.setOnClickListener {
                    Log.i("Like", "Button pressed!")
                    postListener.onUpvoteBtnClick(post)
                }

                dislikeBtn.setOnClickListener {
                    Log.i("Dislike", "Button pressed!")
                    postListener.onDownvoteBtnClick(post)
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
        return if (position==0) ITEM_COMMUNITY_HEADER else ITEM_POST
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == ITEM_COMMUNITY_HEADER){
            val binding = ItemDetailedCommunityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            CommunityViewHolder(binding)
        } else{
            val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            PostViewHolder(binding)
        }
    }

    override fun getItemCount(): Int {
        return postList.size+1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (getItemViewType(position) == ITEM_COMMUNITY_HEADER){
            (holder as CommunityViewHolder).bind(currentCommunity)
        }
        else{
            (holder as PostViewHolder).bind(postList[position-1])
            holder.itemView.setOnClickListener {
                postListener.onPostClick(postList[position-1], currentCommunity)
            }
        }
    }

    fun updatePostList(postList: List<Post>) {
        Log.i("In Update Method", "Local List: ${this.postList}")
        Log.i("In Update Method", "Live List: $postList")
        this.postList = when(filter){
            PostFilter.HOT -> postList.sortedByDescending { it.postId }.toMutableList()
            PostFilter.TOP -> postList.sortedByDescending { it.voteCounter }.toMutableList()
            PostFilter.OLDEST -> postList.sortedBy { it.postId }.toMutableList()
        }
        Log.i("In Update Method", "Local List: ${this.postList}")
        notifyDataSetChanged()
    }


    interface PostListener{
        fun onPostClick(post: Post, community: Community)
        fun onCommunityClick(community: Community)
        fun onUpvoteBtnClick(post: Post)
        fun onDownvoteBtnClick(post: Post)
        fun setFilter(filter: PostFilter)
        fun onFilterClick()
        fun onJoinClick()
        fun onCreateClick()
        fun onShareBtnClick(postId: Int, communityId: Int)
    }

}