package com.d6.android.app.widget

import android.content.Context
import android.support.annotation.IdRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.d6.android.app.R
import com.d6.android.app.activities.UserInfoActivity
import com.d6.android.app.adapters.SquareCommentAdapter
import com.d6.android.app.adapters.SquareImageAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.UnKnowInfoDialog
import com.d6.android.app.models.Comment
import com.d6.android.app.models.Square
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.view_trend_view.view.*
import org.jetbrains.anko.*

/**
 * Created on 2017/12/17.
 * 动态列表
 */
class TrendView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

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
                LayoutInflater.from(context).inflate(R.layout.view_trend_view, this, true)
                rv_images.setHasFixedSize(true)
        //        rv_images.layoutManager = GridLayoutManager(context,3)
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

                commentAdapter.setOnCommentClick {
                    square?.let {
                        Log.i("mOnSquareDetailsClick","dddddd")
                        mOnSquareDetailsClick?.onSquareDetails(it)
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

                ll_comments.setOnClickListener {
                  square?.let {
                      Log.i("mOnSquareDetailsClick","ffffff")
                      mOnSquareDetailsClick?.onSquareDetails(it)
                  }
                }
        //        commentAdapter.setOnCommentClick {
        //            textClickedListener?.onTextClicked()
        //        }

                tv_delete.setOnClickListener {
                    square?.let {
                        deleteAction?.onDelete(it)
                    }
                }
                tv_redflower.setOnClickListener {
                    square?.let {
                        SendFlowerAction?.onSendFlowerClick(it)
                    }
                }

            }

    /**
     * type 1 可以展示删除按钮。
     */
    fun update(square: Square,type:Int?=0) {
        imageAdapter.bindSquare(square)
//        val uid = SPUtils.instance().getString(Const.User.USER_ID)
//        if (TextUtils.equals(square.userid, uid) && type == 1) {//是自己。
//            tv_delete.visible()
//        } else {
//            tv_delete.gone()
//        }
        this.square = square
        headView.hierarchy = getHierarchy(square.sex.toString())
        headView.setImageURI(square.picUrl)

        tv_name.text = square.name

        tv_sex.isSelected = TextUtils.equals("0",square.sex)
//        tv_vip.text = square.userclassesname
        tv_vip.backgroundDrawable = getLevelDrawable(square.userclassesid.toString(),context)

        if(TextUtils.equals("3",square.screen)){
            img_auther.visibility=View.GONE
            img_auther.setImageResource(R.mipmap.renzheng_small)
        }else if(TextUtils.equals("1",square.screen)){
            img_auther.visibility=View.VISIBLE
            img_auther.setImageResource(R.mipmap.video_small)
        }else{
            img_auther.visibility=View.GONE
        }

//        if(TextUtils.equals(square.sex, "1")){
//            tv_vip.visibility = View.VISIBLE
//        }else{
//            tv_vip.visibility = View.GONE
//        }

        if(square.iIsCommmentTop==2){
            tv_sub_title.text = context.getString(R.string.string_newcomments)
        }else{
            val sub = if (square.city.isNullOrEmpty()) {
                square.updatetime?.interval()
            } else {
                String.format("%s | %s",square.updatetime?.interval(),square.city)
            }
            tv_sub_title.text = sub
        }

        if(square.content.isNullOrEmpty()){
            tv_content.visibility = View.GONE
        }else{
            tv_content.visibility = View.VISIBLE
            tv_content.text = square.content
        }

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
            rv_images.addItemDecoration(RxRecyclerViewDividerTool(dip(2)))//SpacesItemDecoration(dip(4),2)
        } else {
            rv_images.layoutManager = GridLayoutManager(context,3)
            rv_images.addItemDecoration(RxRecyclerViewDividerTool(dip(2)))//SpacesItemDecoration(dip(4),3)
        }
        imageAdapter.notifyDataSetChanged()
        tv_appraise.isSelected = TextUtils.equals(square.isupvote,"1")
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

        //iIsSendFlower 大于0送过花，等于0没送过
        tv_redflower.isSelected = if ((square.iIsSendFlower?:0) > 0) {
            true
        } else {
            false
        }
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
            if(square.comments.size>2){
                mComments.addAll(square.comments.subList(0,2))
            }else{
                mComments.addAll(square.comments)
            }
        }
        commentAdapter.setSquareUserId(square.userid.toString(),1)
        commentAdapter.notifyDataSetChanged()
    }

    fun hide(@IdRes viewIdRes: Int) {
        find<View>(viewIdRes).gone()
    }

    fun sendFlowerClick(flowerAction:(square:Square)->Unit){
        this.SendFlowerAction = object :SendFlowerClickListener{
            override fun onSendFlowerClick(square: Square) {
                flowerAction(square)
            }
        }
    }

    fun setPraiseClick(action:(square:Square)->Unit){
        this.action = object : PraiseClickListener{
            override fun onPraiseClick(square: Square) {
                action(square)
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

    fun setOnItemClick(action:(view:View,square:Square)->Unit){
        this.onItemClick = object : OnItemClick{
            override fun onClick(view:View,square: Square) {
                action(view,square)
            }
        }
    }

    fun setOnSquareDetailsClick(action:(square:Square)->Unit) {
        this.mOnSquareDetailsClick = object : OnSquareDetailsClick {
            override fun onSquareDetails(square: Square) {
                    action(square)
            }
        }
    }
    private var action:PraiseClickListener?=null
    private var deleteAction:DeleteClick?=null
    private var onItemClick:OnItemClick?=null
    private var mOnSquareDetailsClick:OnSquareDetailsClick?=null

    private var SendFlowerAction:SendFlowerClickListener?=null

    interface SendFlowerClickListener{
        fun onSendFlowerClick(square: Square)
    }

    interface PraiseClickListener{
        fun onPraiseClick(square: Square)
    }

    interface DeleteClick{
        fun onDelete(square: Square)
    }

    interface OnItemClick{
        fun onClick(view: View,square: Square)
    }

    interface OnSquareDetailsClick{
        fun onSquareDetails(square: Square)
    }

    private var textClickedListener: CustomLinkMovementMethod.TextClickedListener? = null

    fun setOnCommentClick(l:()->Unit) {
        textClickedListener = CustomLinkMovementMethod.TextClickedListener { l() }
    }

}