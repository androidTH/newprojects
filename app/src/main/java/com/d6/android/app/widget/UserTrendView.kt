package com.d6.android.app.widget

import android.content.Context
import android.graphics.Color
import android.support.annotation.IdRes
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.d6.android.app.R
import com.d6.android.app.adapters.SquareCommentAdapter
import com.d6.android.app.adapters.SquareImageAdapter
import com.d6.android.app.models.Comment
import com.d6.android.app.models.Square
import com.d6.android.app.utils.*
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration
import kotlinx.android.synthetic.main.view_user_trend_view.view.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.dip

/**
 * Created on 2017/12/17.
 * 我的动态
 */
class UserTrendView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    private var square: Square? = null
    private val mImages = ArrayList<String>()
    private val imageAdapter by lazy {
        SquareImageAdapter(mImages)
    }
    private val mComments = ArrayList<Comment>()
    private val commentAdapter by lazy {
        SquareCommentAdapter(mComments)
    }
    init {
        LayoutInflater.from(context).inflate(R.layout.view_user_trend_view, this, true)
        rv_images.setHasFixedSize(true)

        rv_images.adapter = imageAdapter
//        rv_images.addItemDecoration(SpacesItemDecoration(dip(4),3))
//
        val recyclerViewClickListener = RecyclerViewClickListener(context,rv_images,RecyclerViewClickListener.OnClickListener {
            square?.let {
                onItemClick?.onClick(rv_images,it)
            }
        })

        rv_images.addOnItemTouchListener(recyclerViewClickListener)
        rv_comment.setHasFixedSize(true)
        rv_comment.layoutManager = LinearLayoutManager(context)
        rv_comment.adapter = commentAdapter
//
        tv_appraise.setOnClickListener {
            square?.let {
                action?.onPraiseClick(it)
            }
        }
//        headView.setOnClickListener {
//            square?.let {
//                val id = it.userid?:""
//                context.startActivity<UserInfoActivity>("id" to id)
//            }
//        }
//        commentAdapter.setOnCommentClick {
//            textClickedListener?.onTextClicked()
//        }
//
        tv_my_square_delete.setOnClickListener {
            square?.let {
                deleteAction?.onDelete(it)
            }
        }
    }

    /**
     * type 1 可以展示删除按钮。
     */
    fun update(square: Square,type:Int?=0) {
        imageAdapter.bindSquare(square)
//
//        val uid = SPUtils.instance().getString(Const.User.USER_ID)
//        if (TextUtils.equals(square.userid, uid) && type == 1) {//是自己。
//            tv_delete.visible()
//        } else {
//            tv_delete.gone()
//        }
        this.square = square
//        headView.setImageURI(square.picUrl)
//        tv_name.text = square.name
//        tv_name.isSelected = TextUtils.equals("0",square.sex)
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
            rv_images.addItemDecoration(RxRecyclerViewDividerTool(dip(2)))
        } else {
            rv_images.layoutManager = GridLayoutManager(context,3)
            rv_images.addItemDecoration(RxRecyclerViewDividerTool(dip(2)))//SpacesItemDecoration(dip(4),3)
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
//        if (!square.classesName.isNullOrEmpty()) {
//            tv_tag.text = String.format("#%s#",square.classesName)
//        } else {
//            tv_tag.text = ""
//        }

        square.commentCount?.let {
            if (it > 0) {
                ll_comments.visible()
                rv_comment.visible()
            } else {
                rv_comment.gone()
                ll_comments.gone()
            }
            if (it > 2) {
                tv_all_comment.text = String.format("查看全部%s条评论",it)
                tv_all_comment.visible()
            } else {
                tv_all_comment.gone()
            }
        }
        mComments.clear()
        if (square.comments != null) {
            mComments.addAll(square.comments)
        }
        commentAdapter.notifyDataSetChanged()
    }

    fun hide(@IdRes viewIdRes: Int) {
        find<View>(viewIdRes).gone()
    }
    fun setPraiseClick(action:(square:Square)->Unit){
        this.action = object : PraiseClickListener{
            override fun onPraiseClick(square: Square) {
                action(square)
            }
        }
    }
    fun setOnItemClick(action:(view:View,square:Square)->Unit){
        this.onItemClick = object : OnItemClick{
            override fun onClick(view:View,square: Square) {
                action(view,square)
            }
        }
    }
    fun setDeleteClick(action:(square:Square)->Unit){
        this.deleteAction = object : DeleteClick{
            override fun onDelete(square: Square) {
                action(square)
            }
        }
    }
    private var action:PraiseClickListener?=null
    private var deleteAction:DeleteClick?=null
    private var onItemClick:OnItemClick?=null
    interface PraiseClickListener{
        fun onPraiseClick(square: Square)
    }

    interface DeleteClick{
        fun onDelete(square: Square)
    }
    interface OnItemClick{
        fun onClick(view: View,square: Square)
    }
    private var textClickedListener: CustomLinkMovementMethod.TextClickedListener? = null
    fun setOnCommentClick(l:()->Unit) {
        textClickedListener = CustomLinkMovementMethod.TextClickedListener { l() }
    }
}