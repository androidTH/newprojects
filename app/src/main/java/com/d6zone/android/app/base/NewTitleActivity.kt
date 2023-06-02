package com.d6zone.android.app.base

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.d6zone.android.app.R
import com.d6zone.android.app.widget.textinlineimage.TextInlineImage
import org.jetbrains.anko.find

/**
 *  带有标题栏的activity
 */
abstract class NewTitleActivity : BaseActivity() {
    //不使用Extensions插件。
    val rootLayout by lazy {
        find<LinearLayout>(R.id.ll_newtoolbar)
    }

    val mIvBackClose by lazy{
        find<ImageView>(R.id.iv_backclose)
    }

    val mTvMiddleTitle by lazy{
        find<TextView>(R.id.tv_middle_title)
    }

    val mTvSamllTitle by lazy{
        find<TextInlineImage>(R.id.tv_small_title)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(R.layout.layout_base_newtitle)
        if (layoutResID > 0) {
            layoutInflater.inflate(layoutResID, rootLayout, true)
        }
    }

    override fun setContentView(view: View?) {
        super.setContentView(R.layout.layout_base_newtitle)
        if (view != null) {
            rootLayout.addView(view)
        }
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        super.setContentView(R.layout.layout_base_newtitle)
        if (view != null && params != null) {
            rootLayout.addView(view, params)
        }
    }

    override fun setTitle(title: CharSequence?) {
        mTvMiddleTitle.text = title
        mTvSamllTitle.visibility = View.GONE
    }

     fun setSmallTitle(smallTitle:String){
         mTvSamllTitle.text = smallTitle
         mTvSamllTitle.visibility = View.VISIBLE
    }

}