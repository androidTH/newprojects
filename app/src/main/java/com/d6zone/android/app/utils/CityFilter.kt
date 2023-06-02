package com.d6zone.android.app.utils

import android.widget.Filter
import com.d6zone.android.app.models.City


/**
 *
 */
class CityFilter:Filter() {
    private val mData=ArrayList<City>()
    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val results = Filter.FilterResults()
        if (constraint == null || constraint.isEmpty()) {
            results.values = mData
            results.count = mData.size
        } else {
            val temp = ArrayList<City>()
            mData.forEach {
                val name = it.name?:""
                if (name.startsWith(constraint)) {
                    temp.add(it)
                } else {
                    val words = name.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val wordCount = words.size
                    // Start at index 0, in case valueText starts with
                    // space(s)
                    for (k in 0 until wordCount) {
                        if (words[k].startsWith(constraint)) {
                            temp.add(it)
                            break
                        }
                    }
                }
            }
            results.values = temp
            results.count = temp.size
        }
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        results?.let {
            callback?.onResult(results.values as ArrayList<City>)
        }
    }

    private var callback :CallBack?=null
    fun setCallBack(callback:(results:ArrayList<City>)->Unit){
        this.callback = object :CallBack{
            override fun onResult(results: ArrayList<City>) {
                callback(results)
            }
        }
    }

    fun update(mData:ArrayList<City>) {
        this.mData.clear()
        this.mData.addAll(mData)
    }

    interface CallBack{
        fun onResult(results:ArrayList<City>)
    }
}