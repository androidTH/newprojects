package com.d6.android.app.adapters

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.d6.android.app.R
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.City

import me.yokeyword.indexablerv.IndexableAdapter

/**
 *
 */

class CityAdapter : IndexableAdapter<City>() {

    override fun onCreateTitleViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_index_city, parent, false)
        return ViewHolder(view)
    }

    override fun onCreateContentViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_city, parent, false)
        return ViewHolder(view)
    }

    override fun onBindTitleViewHolder(holder: RecyclerView.ViewHolder, indexTitle: String) {
        val viewHolder = holder as ViewHolder
        viewHolder.setText(R.id.tv_index, indexTitle)
    }

    override fun onBindContentViewHolder(holder: RecyclerView.ViewHolder, entity: City) {
        val viewHolder = holder as ViewHolder
        val tv_name = viewHolder.bind<TextView>(R.id.tv_name)
        tv_name.isSelected = entity.isSelected
        tv_name.text = entity.name
    }
}
