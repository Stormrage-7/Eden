package com.example.eden.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.eden.R
import com.example.eden.entities.Community
import com.example.eden.databinding.ItemCommunityBinding
import com.example.eden.ui.SelectCommunityActivity

class CommunityAdapter(val context: Context, val clickListener: CommunityClickListener): RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder>() {

    private var communityList = listOf<Community>()
    inner class CommunityViewHolder(val binding: ItemCommunityBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityViewHolder {
        val binding = ItemCommunityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommunityViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return communityList.size
    }

    override fun onBindViewHolder(holder: CommunityViewHolder, position: Int) {
        holder.binding.apply {
            val currentItem = communityList[position]

            if (currentItem.isCustomImage) {
                imageViewCommunity.setImageURI(Uri.parse(currentItem.imageUri))
            }
            else{
                imageViewCommunity.setImageResource(currentItem.imageUri.toInt())
            }

            //For Select Community Activity
            if(context is SelectCommunityActivity){
                joinButton.visibility = View.GONE
            }

            //Setting Join Status
            if (currentItem.isJoined){
                joinButton.text = "Joined"
                joinButton.backgroundTintList = ColorStateList.valueOf(
                    ResourcesCompat.getColor(context.resources, R.color.grey, null)
                )
            }
            else{
                joinButton.text = "Join"
                joinButton.backgroundTintList = ColorStateList.valueOf(
                    ResourcesCompat.getColor(context.resources, R.color.azure, null)
                )
            }

            textViewCommunityName.text = currentItem.communityName
            textViewMemberCounter.text = currentItem.noOfMembers.toString() + " members"
            textViewDescription.text = currentItem.description
            joinButton.setOnClickListener {
                clickListener.onJoinClick(position)
                notifyItemChanged(position)
//                notifyDataSetChanged()
            }
        }
        holder.itemView.setOnClickListener {
            clickListener.onClick(communityList[position])
        }
    }

    fun updateAdapter(communityList: List<Community>){
        this.communityList = communityList
        notifyDataSetChanged()
    }

    interface CommunityClickListener{
        fun onClick(community: Community)
        fun onJoinClick(position: Int)
    }
}