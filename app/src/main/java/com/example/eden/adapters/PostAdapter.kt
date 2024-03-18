package com.example.eden.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.eden.R
import com.example.eden.entities.Post
import com.example.eden.databinding.ItemPostBinding
import com.example.eden.entities.Community
import com.example.eden.entities.User
import com.example.eden.enums.PostFilter
import com.example.eden.models.CommunityModel
import com.example.eden.models.PostModel
import com.example.eden.ui.CommunityDetailedActivity
import com.example.eden.ui.PostInteractionsActivity
import com.example.eden.ui.SearchableActivity
import com.example.eden.ui.UserProfileActivity
import com.example.eden.util.CommunityDiffUtil
import com.example.eden.util.PostDiffUtil
import com.example.eden.util.SafeClickListener
import com.example.eden.util.UriValidator

@SuppressLint("LogNotTimber")
class PostAdapter(
    private val context: Context,
    private val postListener: PostListener
): RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var postList: MutableList<PostModel> = mutableListOf()
    private var communityList: List<CommunityModel> = listOf()
    private var userList: List<User> = listOf()
    private var filter = PostFilter.HOT

    inner class PostViewHolder(val binding: ItemPostBinding): RecyclerView.ViewHolder(binding.root){
        init {
            binding.apply {
                likeBtn.setSafeClickListener {
                    postListener.onUpvoteBtnClick(postList[bindingAdapterPosition])
                    Log.i("Button Click", "postID - ${postList[bindingAdapterPosition].postId}, userID - ${postList[bindingAdapterPosition].posterId}")
                }
                dislikeBtn.setSafeClickListener {
                    postListener.onDownvoteBtnClick(postList[bindingAdapterPosition])
                    Log.i("Button Click", "postID - ${postList[bindingAdapterPosition].postId}, userID - ${postList[bindingAdapterPosition].posterId}")
                }

                bookmarkBtn.setOnClickListener { postListener.onBookmarkClick(postList[bindingAdapterPosition]) }
                shareBtn.setSafeClickListener { postListener.onShareBtnClick(postList[bindingAdapterPosition].postId, postList[bindingAdapterPosition].communityId) }
                textViewUserName.setSafeClickListener { postListener.onUserClick(postList[bindingAdapterPosition].posterId) }
            }
            itemView.setSafeClickListener {
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
        val community: CommunityModel? = communityList.find { it.communityId == communityId }
        val user = userList.find { it.userId == post.posterId}

        holder.binding.apply {

            //COMMUNITY DETAILS
            if (context is CommunityDetailedActivity){
                imageViewCommunity.visibility = View.GONE
                textViewCommunityName.visibility = View.GONE
//                communityBar.visibility = View.INVISIBLE
            }
            else {
                if (community != null) {
                    textViewCommunityName.text = community.communityName
                    if (community.isCustomImage) {
                        if (UriValidator.validate(
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

            if (context !is UserProfileActivity && user != null){
                textViewUserName.text = user.username
            }
            else textViewUserName.visibility = View.GONE

            //TITLE
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
            if(post.bodyText.isEmpty()){
                textViewDescription.visibility = View.GONE
            }
            else{
                textViewDescription.visibility = View.VISIBLE
                textViewDescription.text = postList[position].bodyText
            }

            //UPVOTE AND DOWNVOTE
            textViewVoteCounter.text = post.voteCounter.toString()

            when(post.isBookmarked){
                true -> bookmarkBtn.setIconResource(R.drawable.ic_bookmark_filled)
                false -> bookmarkBtn.setIconResource(R.drawable.ic_bookmark)
            }

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

    private fun setData(newPostList: List<PostModel>){
        val diffUtil = PostDiffUtil(postList, newPostList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
//        if (newPostList.size > postList.size) postListener.scrollToTop()
        postList = newPostList.toMutableList()
        diffResults.dispatchUpdatesTo(this)
    }

    fun updatePostList(newPostList: List<PostModel>) {
        Log.i("In Update Method", "Local List: ${this.postList}")
        Log.i("In Update Method", "Live List: $postList")
        when(filter){
            PostFilter.HOT -> newPostList.sortedByDescending { it.postId }.toMutableList()
            PostFilter.TOP -> newPostList.sortedByDescending { it.voteCounter }.toMutableList()
            PostFilter.OLDEST -> newPostList.sortedBy { it.postId }.toMutableList()
        }
        Log.i("In Update Method", "Local List: ${this.postList}")
        setData(newPostList)
    }

    fun updateCommunityList(newCommunityList: List<CommunityModel>){
        val diffUtil = CommunityDiffUtil(communityList, newCommunityList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        communityList = newCommunityList
        diffResults.dispatchUpdatesTo(this)
    }

    fun updateJoinedCommunityList(joinedCommunitiesList: List<CommunityModel>) {
        this.communityList = joinedCommunitiesList
        Log.i("PostAdapter", "${this.postList}")
    }

    fun updateFilter(filter: PostFilter) {
        this.filter = filter
        val sortedList = this.postList.toMutableList()
        when(this.filter){
            PostFilter.HOT -> sortedList.sortByDescending { it.postId }
            PostFilter.TOP -> sortedList.sortByDescending { it.voteCounter }
            PostFilter.OLDEST -> sortedList.sortBy { it.postId }
        }
        setData(sortedList)
        postListener.scrollToTop()
    }

    fun updateUserList(newUserList: List<User>) {
        userList = newUserList
    }

    private fun View.setSafeClickListener(onSafeCLick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeCLick(it)
        }
        setOnClickListener(safeClickListener)
    }

    interface PostListener{
        fun onPostClick(post: PostModel)
        fun onUserClick(userId: Int)
        fun onCommunityClick(community: CommunityModel)
        fun onUpvoteBtnClick(post: PostModel)
        fun onDownvoteBtnClick(post: PostModel)
        fun onBookmarkClick(post: PostModel)
        fun onShareBtnClick(postId: Int, communityId: Int)
        fun scrollToTop()
    }
}