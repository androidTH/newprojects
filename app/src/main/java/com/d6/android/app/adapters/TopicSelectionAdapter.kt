package com.d6.android.app.adapters


import android.view.View
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder

/**
 *粉丝
 */
class TopicSelectionAdapter(mData:ArrayList<String>): HFRecyclerAdapter<String>(mData, R.layout.item_list_topic) ,View.OnClickListener{
    override fun onClick(v: View?) {

    }

    override fun onBind(holder: ViewHolder, position: Int, data: String) {
        holder.setText(R.id.tv_topicname,data)
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }

}