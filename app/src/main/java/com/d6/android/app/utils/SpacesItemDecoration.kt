package com.d6.android.app.utils

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class SpacesItemDecoration(private val space: Int,private val count:Int = 5) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View,
                                parent: RecyclerView, state: RecyclerView.State?) {
        outRect.left = space/2
        outRect.bottom = space
        if (parent.getChildLayoutPosition(view) % count == 0) {
            outRect.left = 0
            outRect.right=space/2
        }
    }
}