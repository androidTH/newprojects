package com.d6.android.app.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.SelectType
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import kotlinx.android.synthetic.main.activity_filter_vip_level.*

class FilterTypeActivity : TitleActivity() {
    private val types = ArrayList<SelectType>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_vip_level)

        types.add(SelectType("1","救火",false))
        types.add(SelectType("2","征求",false))
        types.add(SelectType("3","急约",false))
        types.add(SelectType("4","旅行约",false))

        mSwipeRefreshLayout.setMode(SwipeRefreshRecyclerLayout.Mode.None)
        mSwipeRefreshLayout.setLayoutManager(LinearLayoutManager(this))
        mSwipeRefreshLayout.setAdapter(TypeAdapter(types))

        btn_sure.setOnClickListener {
            val intent = Intent()
            val array = getSelectedItem()
            intent.putExtra("ids",array[0])
            intent.putExtra("datas",array[1])
            setResult(Activity.RESULT_OK,intent)
            finish()
        }
    }
    private fun getSelectedItem():Array<String> {
        val ids = StringBuilder()
        val names = StringBuilder()
        types.forEach {
            if (it.isSelected) {
                ids.append(it.ids).append(",")
                names.append(it.content).append(",")
            }
        }
        if (ids.isNotEmpty()) {
            ids.deleteCharAt(ids.length-1)
        }
        if (names.isNotEmpty()) {
            names.deleteCharAt(names.length-1)
        }
        return arrayOf(ids.toString(),names.toString())
    }

    private class TypeAdapter(mData:ArrayList<SelectType>) : BaseRecyclerAdapter<SelectType>(mData,R.layout.item_list_type) {
        override fun onBind(holder: ViewHolder, position: Int, data: SelectType) {
            val contentView = holder.bind<TextView>(R.id.tv_content)
            contentView.text = data.content
            if (data.isSelected) {
                contentView.setTextColor(ContextCompat.getColor(context,R.color.orange_f6a))
            } else {
                contentView.setTextColor(ContextCompat.getColor(context,R.color.textColor))
            }

            contentView.setOnClickListener {
                data.isSelected = !data.isSelected
                notifyDataSetChanged()
            }
        }
    }
}