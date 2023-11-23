package com.example.eden

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.eden.entities.Community
import com.example.eden.databinding.ItemCommunityBinding

class CommunityAdapter(val context: Context, val clickListener: CommunityClickListener): RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder>() {

    var communityList = listOf<Community>()
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

            if (currentItem.containsImage) {
                imageViewCommunity.setImageResource(currentItem.imageSrc)
            }
            else{
                imageViewCommunity.setImageResource(R.drawable.icon_logo)
            }

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
                notifyDataSetChanged()
//                if (joinButton.text == "Join") {
//                    joinButton.text = "Joined"
//                    joinButton.backgroundTintList = ColorStateList.valueOf(
//                        ResourcesCompat.getColor(context.resources, R.color.grey, null)
//                    )
//                }
//                else{
//                    joinButton.text = "Join"
//                    joinButton.backgroundTintList = ColorStateList.valueOf(
//                        ResourcesCompat.getColor(context.resources, R.color.azure, null)
//                    )
//                }
            }
        }
    }

    fun updateAdapter(communityList: List<Community>){
        this.communityList = communityList
        notifyDataSetChanged()
    }

    interface CommunityClickListener{
        fun onClick(position: Int)
        fun onJoinClick(position: Int)
    }
}