package com.d6.android.app.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.d6.android.app.R
import com.d6.android.app.adapters.SelfReleaselmageAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.models.Square
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.view_squarebangdan_view.view.*
import org.jetbrains.anko.backgroundDrawable

/**
 * Created on 2017/12/17.
 */
class SquareBangdanListView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    private val mImages = ArrayList<String>()

    private val imageAdapter by lazy {
        SelfReleaselmageAdapter(mImages,1)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_squarebangdan_view, this, true)
//        rv_images.setHasFixedSize(true)
//        rv_images.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//        rv_images.adapter = imageAdapter
    }

    fun update(mData: Square) {
        squarebangdan_one.setImageURI(mData.picUrl)
        squarebangdan_two.setImageURI(mData.picUrl)
        squarebangdan_three.setImageURI(mData.picUrl)
        tv_find_bangdan.setOnClickListener {
               mSendSquareBangDanClick?.let {
                   it.onSquareBangDanClick(mData)
               }
        }
//        headView.setImageURI(voiceChatData.picUrl)
//        Log.i("voicechat","内容：${voiceChatData.content},状态：${voiceChatData.iStatus}")

//        tv_send_voicechat.setOnClickListener {
//            mSendVoiceChatClick?.let {
//                it.onVoiceChatClick(voiceChatData)
//            }
//        }
//
//        tv_voicechat_vip.backgroundDrawable = getLevelDrawable("${voiceChatData.userclassesid}",context)

    }

    fun OnSquareBangDanListener(action:(voiceChatData: Square)->Unit) {
        mSendSquareBangDanClick = object : sendSquareBangDanClickListener {
            override fun onSquareBangDanClick(square: Square) {
                action(square)
            }
        }
    }

    fun setDeleteClick(action:(voiceChatData: Square)->Unit){
        this.deleteAction = object :DeleteClick {
            override fun onDelete(voiceChatData: Square) {
                action(voiceChatData)
            }
        }
    }

    private var mSendSquareBangDanClick:sendSquareBangDanClickListener?=null
    private var deleteAction: DeleteClick?=null

    interface sendSquareBangDanClickListener{
        fun onSquareBangDanClick(square: Square)
    }

    interface DeleteClick{
        fun onDelete(voiceChatData: Square)
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }
}