package com.d6zone.android.app.base.adapters.util

import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.View
import android.widget.TextView

/**
 *
 */
class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val mConvertView = view
    @Suppress("UNCHECKED_CAST")
    fun <T : View> bind(viewId: Int): T {// 通过ViewId得到View
        val viewArray: SparseArray<View>?
        if (mConvertView.tag == null) {
            viewArray = SparseArray()
            mConvertView.tag = viewArray
        } else {
            viewArray = mConvertView.tag as SparseArray<View>
        }

        var childView: View? = viewArray.get(viewId)
        if (childView == null) {
            childView = mConvertView.findViewById(viewId)
            viewArray.put(viewId, childView)
        }
        return childView as T

    }

    /**
     * 设置TextView文字

     * @param resId TextView的id
     * *
     * @param text  文字内容
     */
    fun setText(resId: Int, text: CharSequence?) {
        if (bind<View>(resId) is TextView)
            bind<TextView>(resId).text = text
    }

    fun setSelected(resId: Int, isSelected:Boolean?) {
        isSelected?.let {
            bind<View>(resId).isSelected = it
        }
    }

}