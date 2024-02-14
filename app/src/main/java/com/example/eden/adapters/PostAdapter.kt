package com.example.eden.adapters

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
import com.example.eden.R
import com.example.eden.entities.Post
import com.example.eden.enums.VoteStatus
import com.example.eden.databinding.ItemPostBinding
import com.example.eden.entities.Community
import com.example.eden.entities.relations.PostCommunityCrossRef
import com.example.eden.enums.PostFilter
import com.example.eden.ui.CommunityDetailedActivity
import com.example.eden.ui.HomeScreenActivity
import com.example.eden.ui.PostInteractionsActivity
import com.example.eden.ui.SearchableActivity
import com.example.eden.util.UriValidation

class PostAdapter(
    val context: Context,
    private val postListener: PostListener
): RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    var joinedCommunitiesList: List<Int> = listOf()
    var postList: MutableList<Post> = mutableListOf()
    var communityList: List<Community> = listOf()
    private var filter = PostFilter.HOT

    inner class PostViewHolder(val binding: ItemPostBinding): RecyclerView.ViewHolder(binding.root){
        init {
            binding.apply {
                likeBtn.setOnClickListener {
                    postListener.onUpvoteBtnClick(postList[bindingAdapterPosition])
                }
                dislikeBtn.setOnClickListener {
                    postListener.onDownvoteBtnClick(postList[bindingAdapterPosition])
                }

                shareBtn.setOnClickListener {
                    postListener.onShareBtnClick(postList[bindingAdapterPosition].postId, postList[bindingAdapterPosition].communityId)
                }
            }
            itemView.setOnClickListener {
//                val community = communityList.find { it.communityId == postList[bindingAdapterPosition].communityId }
//                if (community != null) {
//                    postListener.onPostClick(postList[bindingAdapterPosition], community)
//                }
                postListener.onPostClick(postList[bindingAdapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun getItemCount(): Int = postList.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        val communityId = post.communityId
        val community: Community? = communityList.find { it.communityId == communityId }

        holder.binding.apply {

            //COMMUNITY DETAILS
            if (context is CommunityDetailedActivity){
                communityBar.visibility = View.GONE
            }
            else {
                if (community != null) {
                    textViewCommunityName.text = community.communityName
                    if (community.isCustomImage) {
                        if (UriValidation.validate(
                                context,
                                community.imageUri
                            )
                        ) imageViewCommunity.setImageURI(Uri.parse(community.imageUri))
                        else imageViewCommunity.setImageResource(R.drawable.icon_logo)
                    }
                    else imageViewCommunity.setImageResource(community.imageUri.toInt())
                    communityBar.setOnClickListener {
                        postListener.onCommunityClick(community)
                    }
                }
                else {
                    textViewCommunityName.text = ""
                    imageViewCommunity.setImageResource(R.color.white)
                }
            }
            //TITLE
            textViewTitle.text = post.title

            //MEDIA
            if(post.containsImage and UriValidation.validate(context, post.imageUri)){
                imageViewPost.visibility = View.VISIBLE
                imageViewPost.setImageURI(Uri.parse(post.imageUri))
                imageViewPost.scaleType = ImageView.ScaleType.CENTER_CROP
            }
            else imageViewPost.visibility = View.GONE

            //BODY TEXT
            if(post.bodyText.isEmpty()){
                textViewDescription.visibility = View.GONE
            }
            else{
                textViewDescription.visibility = View.VISIBLE
                textViewDescription.text = postList[position].bodyText
            }

            //UPVOTE AND DOWNVOTE
            textViewVoteCounter.text = post.voteCounter.toString()

            likeBtn.setIconResource(post.voteStatus.upvoteIconDrawable)
            dislikeBtn.setIconResource(post.voteStatus.downvoteIconDrawable)
            textViewVoteCounter.setTextColor(ContextCompat.getColor(context, post.voteStatus.textViewColor))

            if ((context is SearchableActivity) or (context is PostInteractionsActivity)){
                textViewVoteCounter.setTextColor(ContextCompat.getColor(context, R.color.black))
                likeBtn.visibility = View.GONE
                dislikeBtn.visibility = View.GONE
                textView.visibility = View.VISIBLE
            }
        }
    }

    fun updatePostList(postList: List<Post>) {
        Log.i("In Update Method", "Local List: ${this.postList}")
        Log.i("In Update Method", "Live List: $postList")
        Log.i("In Update Method", "Joined List: $joinedCommunitiesList")
        this.postList = when(filter){
            PostFilter.HOT -> postList.sortedByDescending { it.postId }.toMutableList()
            PostFilter.TOP -> postList.sortedByDescending { it.voteCounter }.toMutableList()
            PostFilter.OLDEST -> postList.sortedBy { it.postId }.toMutableList()
        }
        Log.i("In Update Method", "Local List: ${this.postList}")
        notifyDataSetChanged()
    }

    fun updateCommunityList(communityList: List<Community>){
        this.communityList = communityList
        notifyDataSetChanged()
    }

    fun updateJoinedCommunityList(joinedCommunitiesList: List<Int>?) {
        if (joinedCommunitiesList != null) {
            this.joinedCommunitiesList = joinedCommunitiesList
            Log.i("PostAdapter", "${this.postList}")
            this.postList =
                postList.filter { post -> this.joinedCommunitiesList.contains(post.communityId) }.toMutableList()
        }
        Log.i("PostAdapter", "${this.postList}")
        notifyDataSetChanged()
    }

    fun updateFilter(filter: PostFilter) {
        this.filter = filter
        when(this.filter){
            PostFilter.HOT -> postList.sortByDescending { it.postId }
            PostFilter.TOP -> postList.sortByDescending { it.voteCounter }
            PostFilter.OLDEST -> postList.sortBy { it.postId }
        }
        notifyDataSetChanged()
    }

    interface PostListener{
        fun onPostClick(post: Post)
        fun onCommunityClick(community: Community)
        fun onUpvoteBtnClick(post: Post)
        fun onDownvoteBtnClick(post: Post)
        fun onShareBtnClick(postId: Int, communityId: Int)
    }

}