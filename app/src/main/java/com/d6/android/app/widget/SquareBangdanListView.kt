package com.d6.android.app.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.Toast
import com.d6.android.app.R
import com.d6.android.app.activities.UserInfoActivity
import com.d6.android.app.adapters.SelfReleaselmageAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.showBlur
import com.d6.android.app.models.Square
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.view_squarebangdan_view.view.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.startActivity

/**
 * Created on 2017/12/17.
 */
class SquareBangdanListView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_squarebangdan_view, this, true)
//        rv_images.setHasFixedSize(true)
//        rv_images.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//        rv_images.adapter = imageAdapter
    }

    fun update(mData: Square) {
        var mHightList = mData.highList
        if(mHightList.size>0&&mHightList.size<=3){
            var mLoveFansOne = mHightList.get(0)
            if(mLoveFansOne.iListSetting==2){
                squarebangdan_one.showBlur(mLoveFansOne.sPicUrl)
                tv_squarebangdan_one_name.text = "匿名"
            }else{
                squarebangdan_one.setImageURI(mLoveFansOne.sPicUrl)
                tv_squarebangdan_one_name.text = "${mLoveFansOne.sSendUserName}"
            }
            tv_squarebangdan_one_sex.isSelected = TextUtils.equals("0","${mLoveFansOne.sSex}")
            tv_squarebangdan_one_vip.backgroundDrawable = getLevelDrawable("${mLoveFansOne.userclassesid}",context)
            if(mLoveFansOne.iAllLovePoint!=0){
                tv_squarebangdan_one_receivedliked.text = "收到${mLoveFansOne.iAllLovePoint}"
            }
//            rl_squarebangdan_one.setOnClickListener {
//                isBaseActivity {
//                    mSendSquareBangDanClick?.let {
//                        it.onSquareBangDanClick(mData)
//                    }
////                    val id = mLoveFansOne.iUserid
////                    it.startActivity<UserInfoActivity>("id" to "${id}")
//                }
//            }
        }

        if(mHightList.size>=2){
            var mLoveFansTwo = mHightList.get(1)
            if(mLoveFansTwo.iListSetting==2){
                squarebangdan_two.showBlur(mLoveFansTwo.sPicUrl)
                tv_squarebangdan_two_name.text = "匿名"
            }else{
                squarebangdan_two.setImageURI(mLoveFansTwo.sPicUrl)
                tv_squarebangdan_two_name.text = "${mLoveFansTwo.sSendUserName}"
            }
            tv_squarebangdan_two_sex.isSelected = TextUtils.equals("0","${mLoveFansTwo.sSex}")
            tv_squarebangdan_two_vip.backgroundDrawable = getLevelDrawable("${mLoveFansTwo.userclassesid}",context)

//            rl_squarebangdan_two.setOnClickListener {
//                isBaseActivity {
//                    mSendSquareBangDanClick?.let {
//                        it.onSquareBangDanClick(mData)
//                    }
////                    val id = mLoveFansTwo.iUserid?:""
////                    it.startActivity<UserInfoActivity>("id" to "${id}")
//                }
//            }
        }

        if(mHightList.size==3){
            var mLoveFansThree = mHightList.get(2)
            if(mLoveFansThree.iListSetting==2){
                squarebangdan_three.showBlur(mLoveFansThree.sPicUrl)
                tv_squarebangdan_two_name.text = "匿名"
            }else{
                squarebangdan_three.setImageURI(mLoveFansThree.sPicUrl)
                tv_squarebangdan_three_name.text = "${mLoveFansThree.sSendUserName}"
            }

            tv_squarebangdan_three_sex.isSelected = TextUtils.equals("0","${mLoveFansThree.sSex}")
            tv_squarebangdan_three_vip.backgroundDrawable = getLevelDrawable("${mLoveFansThree.userclassesid}",context)
//            rl_squarebangdan_three.setOnClickListener {
//                isBaseActivity {
//                    mSendSquareBangDanClick?.let {
//                        it.onSquareBangDanClick(mData)
//                    }
////                    val id = mLoveFansThree.iUserid
////                    it.startActivity<UserInfoActivity>("id" to "${id}")
//                }
//            }
        }

        tv_find_bangdan.setOnClickListener {
               mSendSquareBangDanClick?.let {
                   it.onSquareBangDanClick(mData)
               }
        }
        rl_square_bangdan_list.setOnClickListener {
            mSendSquareBangDanClick?.let {
                it.onSquareBangDanClick(mData)
            }
        }
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