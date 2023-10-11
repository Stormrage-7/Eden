package com.example.eden

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eden.databinding.ItemPostBinding

class PostAdapter(
    private var postList: List<Post>, private val context: Context): RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private lateinit var postListener: OnPostClickListener

    inner class PostViewHolder(val binding: ItemPostBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        //val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.binding.apply {
            textViewTitle.text = postList[position].title
            if(postList[position].containsImage) imageViewPost.visibility = View.VISIBLE
            else imageViewPost.visibility = View.GONE
            textViewDescription.text = postList[position].bodyText
            textViewVoteCounter.text = postList[position].voteCounter.toString()
            likeBtn.setOnClickListener {
                textViewVoteCounter.text = (textViewVoteCounter.text.toString().toInt() + 1).toString()
            }

            dislikeBtn.setOnClickListener {
                textViewVoteCounter.text = (textViewVoteCounter.text.toString().toInt() - 1).toString()
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

    interface OnPostClickListener{
        fun onPostClick(position: Int)
        fun onPostLongClick(position: Int)
    }


}