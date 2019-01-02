package com.d6.android.app.widget

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.d6.android.app.R
import com.d6.android.app.activities.UserInfoActivity
import com.d6.android.app.adapters.SquareImageAdapter
import com.d6.android.app.models.Square
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.view_trend_detail_view.view.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity

/**
 * Created on 2017/12/17.
 */
class TrendDetailView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    private var square: Square? = null
    private val mImages = ArrayList<String>()
    private val imageAdapter by lazy {
        SquareImageAdapter(mImages)
    }
    init {
        LayoutInflater.from(context).inflate(R.layout.view_trend_detail_view, this, true)

        rv_images.setHasFixedSize(true)
//        rv_images.layoutManager = GridLayoutManager(context,3)
        rv_images.adapter = imageAdapter
//        rv_images.addItemDecoration(SpacesItemDecoration(dip(4),3))

        tv_appraise.setOnClickListener {
            square?.let {
                action?.onPraiseClick(it)
            }
        }
        headView.setOnClickListener {
            square?.let {
                val id = it.userid?:""
                context.startActivity<UserInfoActivity>("id" to id)
            }
        }
        tv_redflower.setOnClickListener {
            square?.let {
                sendFlowerClick?.onSendFlowerClick(it)
            }
        }
    }

    fun update(square: Square) {
        this.square = square
        imageAdapter.bindSquare(square)
        headView.setImageURI(square.picUrl)
        tv_name.text = square.name
        val sub = if (square.city.isNullOrEmpty()) {
            square.updatetime.toTime("MM.dd")
        } else {
            String.format("%s | %s",square.updatetime.toTime("MM.dd"),square.city)
        }
        tv_sub_title.text = sub
        tv_content.text = square.content
        if (square.imgUrl.isNullOrEmpty()) {
            rv_images.gone()
        } else {
            rv_images.visible()
        }
        mImages.clear()
        val images = square.imgUrl?.split(",")
        if (images != null) {
            mImages.addAll(images.toList())
        }
        val d = rv_images.getItemDecorationAt(0)
        if (d != null) {
            rv_images.removeItemDecoration(d)
        }
        if (mImages.size == 1 || mImages.size == 2 || mImages.size == 4) {
            rv_images.layoutManager = GridLayoutManager(context,2)
            rv_images.addItemDecoration(SpacesItemDecoration(dip(4),2))
        } else {
            rv_images.layoutManager = GridLayoutManager(context,3)
            rv_images.addItemDecoration(SpacesItemDecoration(dip(4),3))
        }
        imageAdapter.notifyDataSetChanged()
//        tv_appraise.text = square.appraiseCount.toString()
        tv_appraise.isSelected = TextUtils.equals(square.isupvote,"1")
//        tv_comment.text = square.commentCount.toString()
        tv_comment.text = if ((square.commentCount?:0) > 0) {
            square.commentCount.toString()
        } else {
            ""
        }
        tv_appraise.text = if ((square.appraiseCount ?:0)> 0) {
            square.appraiseCount.toString()
        } else {
            ""
        }

        tv_redflower.text = if((square.iFlowerCount?:0)>0){
            square.iFlowerCount.toString()
        }else{
            ""
        }

        if (!square.classesName.isNullOrEmpty()) {
            tv_tag.text = String.format("#%s#",square.classesName)
        } else {
            tv_tag.text = ""
        }

        square.commentCount?.let {
            if (it > 0) {
                comment_line.visible()
            } else {
                comment_line.gone()
            }
        }
    }

    fun setPraiseClick(action:(square:Square)->Unit){
        this.action = object : Action{
            override fun onPraiseClick(square: Square) {
                action(square)
            }
        }
    }

    fun setOnSendFlowerClick(sendFlowerClick:(square:Square)->Unit){
        this.sendFlowerClick = object :DoSendFlowerClick{
            override fun onSendFlowerClick(square: Square) {
                sendFlowerClick(square)
            }
        }
    }

    private var action:Action?=null
    private var sendFlowerClick:DoSendFlowerClick?=null

    interface Action{
        fun onPraiseClick(square: Square)
    }

    interface DoSendFlowerClick{
        fun onSendFlowerClick(square:Square)
    }
}