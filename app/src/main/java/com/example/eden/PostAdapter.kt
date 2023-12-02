package com.example.eden

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
import com.example.eden.entities.Post
import com.example.eden.enums.VoteStatus
import com.example.eden.databinding.ItemPostBinding

class PostAdapter(
    val context: Context,
    private val clickListener: PostClickListener): RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    var postList: List<Post> = listOf()

    inner class PostViewHolder(val binding: ItemPostBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.binding.apply {
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

                // INITIAL SETTING
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

                // CHANGES TO THE VOTE
            likeBtn.setOnClickListener {
                Log.i("Like", "Button pressed!")
                clickListener.onUpvoteBtnClick(position)
            }

            dislikeBtn.setOnClickListener {
                Log.i("Dislike", "Button pressed! ${position}")
                clickListener.onDownvoteBtnClick(position)
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
    }

    fun update(postList: List<Post>) {
        Log.i("In Update Method", "Local List: ${this.postList}")
        Log.i("In Update Method", "Live List: $postList")
        this.postList = postList
        Log.i("In Update Method", "Local List: ${this.postList}")
        notifyDataSetChanged()
    }

    interface PostClickListener{
        fun onPostClick(position: Int)
        fun onUpvoteBtnClick(position: Int)
        fun onDownvoteBtnClick(position: Int)
        fun onPostLongClick(position: Int)
    }


}