package com.example.eden.util

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eden.R
import com.example.eden.adapters.CommunityWithPostsAdapter
import com.example.eden.adapters.PostAdapter

class StickyHeaderItemDecorator {

    private lateinit var listener: StickyHeaderInterface
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CommunityWithPostsAdapter
    private val currentHeaderViewMap: MutableMap<Int, Boolean> by lazy { mutableMapOf() }
    private var stickyHeaderContainer: LinearLayout? = null

    fun attachRecyclerView(
        listener: StickyHeaderInterface,
        recyclerView: RecyclerView,
        adapter: CommunityWithPostsAdapter
    ) {
        this.listener = listener
        this.recyclerView = recyclerView
        this.adapter = adapter
        initContainer()
        clearHeaderViews()
        refreshHeader()
    }

    private fun initContainer() {
//        stickyHeaderContainer =
//            (recyclerView.parent as? ViewGroup)?.findViewById(R.id.filterStickyHeader)
//        if (stickyHeaderContainer == null) {
//            // RecyclerViews parent should be FrameLayout or RelativeLayout to add sticky header at top
//            val linearLayout = LinearLayout(recyclerView.context)
//            stickyHeaderContainer = linearLayout
//            val params = ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//            stickyHeaderContainer?.id = R.id.filterStickyHeader
//            stickyHeaderContainer?.orientation = LinearLayout.VERTICAL
//            (recyclerView.parent as? ViewGroup)?.addView(stickyHeaderContainer, params)
//        }
//        recyclerView.addOnScrollListener(onScrollChangeListener)
    }

    private fun clearHeaderViews() {
        currentHeaderViewMap.clear()
        stickyHeaderContainer?.removeAllViews()
    }

    private fun addHeaderViewFromPosition(position: Int) {
        if (currentHeaderViewMap[position] == true) return // return if header already added

        stickyHeaderContainer?.let {
            val vh = adapter.createViewHolder(it, adapter.getItemViewType(position))
            adapter.bindViewHolder(vh, position)
            val view = vh.itemView
            view.tag = position
            it.addView(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            currentHeaderViewMap[position] = true
            recyclerView.post { it.requestLayout() }
        }
    }

    private fun removeHeaderViewFromPosition(position: Int) {
        if (currentHeaderViewMap[position] != true) return // already removed

        val view = stickyHeaderContainer?.findViewWithTag<View>(position)
        view?.let {
            stickyHeaderContainer?.removeView(it)
        }
        currentHeaderViewMap[position] = false
    }

    private fun drawHeaders() {
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager?
        var topChildPosition = layoutManager?.findFirstVisibleItemPosition() ?: 0
        var i = 0
        while (i <= topChildPosition) {
            if (i == RecyclerView.NO_POSITION) break
            if (listener.isHeader(i)) {
                removeExistingHeaders(i) // Can be removed if needed stacked type sticky headers
                addHeaderViewFromPosition(i)
                topChildPosition++
            }
            i++
        }
    }

    private fun removeExistingHeaders(i: Int) {
        currentHeaderViewMap.forEach { (key, value) ->
            if ((!listener.isHeader(key) || key < i) && value) {
                removeHeaderViewFromPosition(key)
            }
        }
    }

    private fun removeInvalidHeaders() {
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager?
        val topChildPosition =
            layoutManager?.findFirstVisibleItemPosition() ?: 0
        currentHeaderViewMap.forEach { (key, value) ->
            if ((!listener.isHeader(key) || key > topChildPosition) && value) {
                removeHeaderViewFromPosition(key)
            }
        }
    }

    private val onScrollChangeListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                refreshHeader()
            }
        }

    fun refreshHeader() {
        recyclerView.post {
            removeInvalidHeaders()
            drawHeaders()
        }
    }

    fun clearReferences() {
        recyclerView.removeOnScrollListener(onScrollChangeListener)
    }

    interface StickyHeaderInterface {
        fun isHeader(itemPosition: Int): Boolean
    }
}