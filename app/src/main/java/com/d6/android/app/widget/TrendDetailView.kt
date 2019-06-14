package com.d6.android.app.widget

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.d6.android.app.R
import com.d6.android.app.activities.UserInfoActivity
import com.d6.android.app.adapters.SquareImageAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.UnKnowInfoDialog
import com.d6.android.app.models.Square
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.view_trend_detail_view.view.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.bundleOf
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

        root_commentlayout.setOnClickListener {
            square?.let {
                actiToggleSoftClick?.onToggleSoftInput(it)
            }
        }

        tv_appraise.setOnClickListener {
            square?.let {
                action?.onPraiseClick(it)
            }
        }
        headView.setOnClickListener {
            square?.let {
                val id = it.userid?:""
                if(it.iIsAnonymous==1){
                    var mUnknowDialog = UnKnowInfoDialog()
                    mUnknowDialog.arguments = bundleOf("otheruserId" to id)
                    mUnknowDialog.show((context as BaseActivity).supportFragmentManager,"unknowDialog")
                }else{
                    context.startActivity<UserInfoActivity>("id" to id)
                }
            }
        }
        tv_redflower.setOnClickListener {
            square?.let {
                sendFlowerClick?.onSendFlowerClick(it)
            }
        }

        tv_comment.setOnClickListener {
            square?.let {
                actionCommentClick?.onCommentClick(it)
            }
        }

        tv_delete.setOnClickListener {
            square?.let {
                mDeleteClick?.onDelete(it)
            }
        }

    }

    fun update(square: Square) {
        this.square = square
        imageAdapter.bindSquare(square)
        headView.hierarchy = getHierarchy(square.sex.toString())
        headView.setImageURI(square.picUrl)
        tv_name.text = square.name
        tv_sex.isSelected = if(TextUtils.equals("0",square.sex)) true else false

        if(TextUtils.equals("3",square.screen)){
            img_auther.visibility= View.GONE
            img_auther.setImageResource(R.mipmap.renzheng_small)
        }else if(TextUtils.equals("1",square.screen)){
            img_auther.visibility= View.VISIBLE
            img_auther.setImageResource(R.mipmap.video_small)
        }else{
            img_auther.visibility= View.GONE
        }

        if (square.userclassesname.toString().startsWith("入门")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.gril_cj)
        } else if (square.userclassesname.toString().startsWith("中级")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.gril_zj)
        } else if (square.userclassesname.toString().startsWith("优质")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.gril_gj)
        } else if (square.userclassesname.toString().startsWith("普通")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_ordinary)
        } else if (square.userclassesname.toString().startsWith("白银")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_silver)
        } else if (square.userclassesname.toString().startsWith("黄金")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_gold)
        } else if (square.userclassesname.toString().startsWith( "钻石")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_zs)
        } else if (square.userclassesname.toString().startsWith("私人")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_private)
        }else if (square.userclassesname.toString().startsWith("游客")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.youke_icon)
        }else if (square.userclassesname.toString().startsWith("入群")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.ruqun_icon)
        }

        val sub = if (square.city.isNullOrEmpty()) {
            square.updatetime?.interval()
        } else {
            String.format("%s | %s",square.updatetime?.interval(),square.city)
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

        tv_redflower.isSelected = if ((square.iIsSendFlower?:0) > 0) {
            true
        } else {
            false
        }

        if (!square.classesName.isNullOrEmpty()) {
            tv_tag.text = String.format("#%s#",square.classesName)
        } else {
            tv_tag.text = ""
        }

//        square.commentCount?.let {
//            if (it > 0) {
//                comment_line.visible()
//            } else {
//                comment_line.gone()
//            }
//        }
    }

    fun updateFlowerCount(square: Square){
        tv_redflower.text = if((square.iFlowerCount?:0)>0){
            square.iFlowerCount.toString()
        }else{
            ""
        }
        tv_redflower.isSelected = if ((square.iIsSendFlower?:0) > 0) {
            true
        } else {
            false
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

    fun setOnCommentClick(action:(square:Square)->Unit){
        this.actionCommentClick = object : DoCommentClick{
            override fun onCommentClick(square: Square) {
                action(square)
            }
        }
    }

    fun setOnSoftInputClick(action:(square:Square)->Unit){
        this.actiToggleSoftClick = object : onToggleSoftInput{
            override fun onToggleSoftInput(square: Square) {
                action(square)
            }
        }
    }

    fun setDeletClick(action:(square:Square)->Unit) {
        this.mDeleteClick = object : DeleteClick {
            override fun onDelete(square: Square) {
                action(square)
            }
        }
    }

    private var action:Action?=null
    private var actionCommentClick:DoCommentClick?=null
    private var actiToggleSoftClick:onToggleSoftInput?=null
    private var sendFlowerClick:DoSendFlowerClick?=null
    private var mDeleteClick:DeleteClick?=null

    interface Action{
        fun onPraiseClick(square: Square)
    }

    interface DoSendFlowerClick{
        fun onSendFlowerClick(square:Square)
    }

    interface DoCommentClick{
        fun onCommentClick(square: Square)
    }

    interface  onToggleSoftInput{
        fun onToggleSoftInput(square: Square)
    }

    interface DeleteClick{
        fun onDelete(square: Square)
    }
}