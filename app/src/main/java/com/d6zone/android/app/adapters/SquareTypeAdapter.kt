package com.d6zone.android.app.adapters

import android.support.v4.content.ContextCompat
import android.widget.TextView
import com.d6zone.android.app.R
import com.d6zone.android.app.base.adapters.BaseRecyclerAdapter
import com.d6zone.android.app.base.adapters.util.ViewHolder
import com.d6zone.android.app.models.TopicBean
import com.d6zone.android.app.utils.setLeftDrawable

/**
 *
 */
class SquareTypeAdapter(mData:ArrayList<TopicBean>): BaseRecyclerAdapter<TopicBean>(mData, R.layout.item_square_type) {
    override fun onBind(holder: ViewHolder, position: Int, data: TopicBean) {
        val contentView = holder.bind<TextView>(R.id.tv_square_type)
        contentView.text = data.sTopicName
        if(data.mResId!=-1){
            var drawable = ContextCompat.getDrawable(context,data.mResId)
            setLeftDrawable(drawable,contentView)
        }else{
            var drawable = ContextCompat.getDrawable(context,R.mipmap.tag_list_bigicon)
            setLeftDrawable(drawable,contentView)
        }
    }
}