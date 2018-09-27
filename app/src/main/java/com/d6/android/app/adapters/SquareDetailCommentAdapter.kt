package com.d6.android.app.adapters

import android.support.v4.content.ContextCompat
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.activities.UserInfoActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.dialogs.CommentDelDialog
import com.d6.android.app.models.Comment
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.CustomLinkMovementMethod
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.SpanBuilder
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.startActivity

/**
 *
 */
class SquareDetailCommentAdapter(mData: ArrayList<Comment>) : HFRecyclerAdapter<Comment>(mData, R.layout.item_list_square_detail_comment) {


    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val holder = super.onCreateViewHolder(parent, viewType)
        if (viewType == TYPE_NORMAL) {
            val contentView = holder.bind<TextView>(R.id.tv_content)
            contentView.movementMethod = CustomLinkMovementMethod()
        }
        return holder
    }

    override fun onBind(holder: ViewHolder, position: Int, data: Comment) {
        val headView = holder.bind<SimpleDraweeView>(R.id.headView)
        headView.setImageURI(data.picUrl)
        headView.setOnClickListener {
            val id = data.userId ?: ""
            context.startActivity<UserInfoActivity>("id" to id)
        }
        holder.setText(R.id.tv_name, data.name)
        val contentView = holder.bind<TextView>(R.id.tv_content)
        val spanText = if (data.replyUserId.isNullOrEmpty()) {
            //这里多余了。什么都没做只是转换成了下类型
            SpanBuilder(data.content ?: "")
                    .build()
        } else {
            val content = String.format("回复%s:%s", data.replyName, data.content)
            val length1 = data.replyName?.length ?: 0
            SpanBuilder(content)
                    .click(2, 2 + length1, TextClickableSpan(data.replyUserId))
                    .build()
        }
        contentView.text = spanText

        val tv_del_myself_comment = holder.bind<TextView>(R.id.tv_del_myselfcomment)
        if(data.userId == userId){
            tv_del_myself_comment.visibility = View.VISIBLE
        }else{
            tv_del_myself_comment.visibility = View.GONE
        }
        tv_del_myself_comment.setOnClickListener {
            deleteAction?.onDelete(data)
        }
    }

    private inner class TextClickableSpan(private val id: String?) : ClickableSpan() {

        override fun onClick(widget: View) {
            System.err.println("-view--$widget")

        }

        override fun updateDrawState(ds: TextPaint) {
            ds.color = ContextCompat.getColor(context, R.color.color_369)
            ds.isUnderlineText = false
        }
    }

    private var deleteAction:DeleteClick?=null

    interface DeleteClick{
        fun onDelete(comment: Comment)
    }

    fun setDeleteClick(action:(comment:Comment)->Unit){
        this.deleteAction = object : SquareDetailCommentAdapter.DeleteClick {
            override fun onDelete(comment: Comment) {
                action(comment)
            }
        }
    }

//    val commentDelDialog = CommentDelDialog()
//    commentDelDialog.arguments = bundleOf("data" to it)
//    commentDelDialog.show((context as BaseActivity).supportFragmentManager, "action")
//    commentDelDialog.setDialogListener { p, s ->
//        if (p == 1) {
//            delete(it)
//        }
//    }
}