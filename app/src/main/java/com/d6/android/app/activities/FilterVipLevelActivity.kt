package com.d6.android.app.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.d6.android.app.R
import com.d6.android.app.adapters.UserLevelAdapter
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.UserLevel
import com.d6.android.app.net.Request
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import kotlinx.android.synthetic.main.activity_filter_vip_level.*

class FilterVipLevelActivity : TitleActivity() {
    private val mLevels = ArrayList<UserLevel>()
    private val levelAdapter by lazy {
        UserLevelAdapter(mLevels)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_vip_level)
        mSwipeRefreshLayout.setMode(SwipeRefreshRecyclerLayout.Mode.None)
        mSwipeRefreshLayout.setLayoutManager(LinearLayoutManager(this))
        mSwipeRefreshLayout.setAdapter(levelAdapter)
        btn_sure.setOnClickListener {
            val intent = Intent()
            val array = getSelectedItem()
            intent.putExtra("ids", array[0])
            intent.putExtra("datas", array[1])
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        dialog()
        getData()
    }

    private fun getData() {
        Request.getUserLevels().request(this) { _, data ->
            mLevels.clear()
            data?.let {
                mLevels.addAll(it)
            }
            levelAdapter.notifyDataSetChanged()
        }
    }

    private fun getSelectedItem(): Array<String> {
        val ids = StringBuilder()
        val names = StringBuilder()
        mLevels.forEach {
            if (it.isSelected) {
                ids.append(it.ids).append(",")
                names.append(String.format("%s",it.classesname)).append(",")
            }
        }
        if (ids.isNotEmpty()) {
            ids.deleteCharAt(ids.length - 1)
        }
        if (names.isNotEmpty()) {
            names.deleteCharAt(names.length - 1)
        }
        return arrayOf(ids.toString(), names.toString())
    }
}
