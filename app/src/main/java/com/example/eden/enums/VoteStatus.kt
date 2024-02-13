package com.example.eden.enums

import com.example.eden.R

enum class VoteStatus (val upvoteIconDrawable: Int,
                       val downvoteIconDrawable: Int,
                       val textViewColor: Int) {
    UPVOTED (R.drawable.icon_upvote_new_filled, R.drawable.icon_downvote_new, R.color.green),
    DOWNVOTED (R.drawable.icon_upvote_new, R.drawable.icon_downvote_new_filled, R.color.red),
    NONE (R.drawable.icon_upvote_new, R.drawable.icon_downvote_new, R.color.black)
}