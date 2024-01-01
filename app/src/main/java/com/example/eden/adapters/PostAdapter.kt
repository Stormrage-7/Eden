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
import com.example.eden.ui.CommunityDetailedActivity
import com.example.eden.ui.HomeScreenActivity
import com.example.eden.ui.SearchableActivity

class PostAdapter(
    val context: Context,
    private val postListener: PostListener
): RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    var joinedCommunitiesList: List<Int> = listOf()
    var postList: List<Post> = listOf()
    var communityList: List<Community> = listOf()
    var postCommunityCrossRefList: List<PostCommunityCrossRef> = listOf()
    lateinit var currentCommunity: Community

    inner class PostViewHolder(val binding: ItemPostBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        val communityId = post.communityId

        val community: Community = if (context is HomeScreenActivity || context is SearchableActivity) communityList.find { it.communityId == communityId }!!
        else currentCommunity


        holder.binding.apply {
            //COMMUNITY DETAILS

            if (context is CommunityDetailedActivity){
                communityBar.visibility = View.GONE
            }
            else {
                textViewCommunityName.text = community.communityName
                if (community.isCustomImage) imageViewCommunity.setImageURI(Uri.parse(community.imageUri))
                else imageViewCommunity.setImageResource(community.imageUri.toInt())
                communityBar.setOnClickListener {
                    postListener.onCommunityClick(community)
                }
            }
            //TITLE
            textViewTitle.text = postList[position].title

            //MEDIA
            if(postList[position].containsImage){
                imageViewPost.visibility = View.VISIBLE
                imageViewPost.setImageURI(Uri.parse(postList[position].imageUri))
                imageViewPost.scaleType = ImageView.ScaleType.FIT_XY
            }
            else imageViewPost.visibility = View.GONE

            //BODY TEXT
            if(postList[position].bodyText.isEmpty()){
                textViewDescription.visibility = View.GONE
            }
            else{
                textViewDescription.visibility = View.VISIBLE
                textViewDescription.text = postList[position].bodyText
            }

            //UPVOTE AND DOWNVOTE
            Log.i("Binding View", "Binding View to Holder!")
            textViewVoteCounter.text = postList[position].voteCounter.toString()

            if (postList[position].voteStatus == VoteStatus.UPVOTED) {
                likeBtn.setImageResource(R.drawable.upvote_circle_up_green_24)
                dislikeBtn.setImageResource(R.drawable.downvote_circle_down_24)
                textViewVoteCounter.setTextColor(ContextCompat.getColor(context, R.color.green))

            }
            else if (postList[position].voteStatus == VoteStatus.DOWNVOTED){
                dislikeBtn.setImageResource(R.drawable.downvote_circle_down_red_24)
                likeBtn.setImageResource(R.drawable.upvote_circle_up_24)
                textViewVoteCounter.setTextColor(ContextCompat.getColor(context, R.color.red))
            }
            else{
                likeBtn.setImageResource(R.drawable.upvote_circle_up_24)
                dislikeBtn.setImageResource(R.drawable.downvote_circle_down_24)
                textViewVoteCounter.setTextColor(ContextCompat.getColor(context, R.color.black))
            }

            if (context is SearchableActivity){
                textViewVoteCounter.setTextColor(ContextCompat.getColor(context, R.color.black))
                likeBtn.visibility = View.GONE
                dislikeBtn.visibility = View.GONE
                shareBtn.visibility = View.GONE
                textView.visibility = View.VISIBLE
            }

                // CHANGES TO THE VOTE
            likeBtn.setOnClickListener {
                Log.i("Like", "Button pressed!")
                postListener.onUpvoteBtnClick(post)
            }

            dislikeBtn.setOnClickListener {
                Log.i("Dislike", "Button pressed! ${position}")
                postListener.onDownvoteBtnClick(post)
            }

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

        holder.itemView.setOnClickListener {
            postListener.onPostClick(post, community)
        }
    }

    fun updatePostList(postList: List<Post>) {
        Log.i("In Update Method", "Local List: ${this.postList}")
        Log.i("In Update Method", "Live List: $postList")
        Log.i("In Update Method", "Joined List: $joinedCommunitiesList")
        this.postList = postList
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
            this.postList = postList.filter { post -> this.joinedCommunitiesList.contains(post.communityId) }
        }
        Log.i("PostAdapter", "${this.postList}")
        notifyDataSetChanged()
    }

    interface PostListener{
        fun getCommunityIdFromPostId(position: Int): Int
        fun onPostClick(post: Post, community: Community)
        fun onCommunityClick(community: Community)
        fun onUpvoteBtnClick(post: Post)
        fun onDownvoteBtnClick(post: Post)
        fun onPostLongClick(position: Int)
    }

}