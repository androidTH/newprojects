package com.d6.android.app.adapters

import com.contrarywind.adapter.WheelAdapter

class ArrayWheelAdapter(val mData:ArrayList<String>):WheelAdapter<String> {
    override fun indexOf(p0: String?): Int {
        return mData.indexOf(p0)
    }

    override fun getItemsCount(): Int {
        return mData.size
    }

    override fun getItem(p0: Int): String {
        if (p0 >= 0 && p0 < mData.size) {
            return mData[p0]
        }
        return ""
    }
}