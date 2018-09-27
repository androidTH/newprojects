package com.d6.android.app.adapters

import android.support.v4.content.ContextCompat
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.activities.UserInfoActivity
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.dialogs.CommentDelDialog
import com.d6.android.app.dialogs.SquareActionDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Comment
import com.d6.android.app.models.Square
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.UserTrendView
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity

/**
 *
 */
class TrendCommentAdapter(mData:ArrayList<Comment>):HFRecyclerAdapter<Comment>(mData, R.layout.item_list_trend_comment) {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val holder = super.onCreateViewHolder(parent, viewType)
        if (viewType==TYPE_NORMAL) {
            val contentView = holder.bind<TextView>(R.id.tv_content)
            contentView.movementMethod = CustomLinkMovementMethod()
        }
        return holder
    }
    override fun onBind(holder: ViewHolder, position: Int, data: Comment) {
        val headView = holder.bind<SimpleDraweeView>(R.id.headView)
        headView.setImageURI(data.picUrl)
        holder.setText(R.id.tv_name,data.name)
        holder.setText(R.id.tv_time, data.createTime.toTime("MM.dd"))
        headView.setOnClickListener {
            val id = data.userId ?: ""
            context.startActivity<UserInfoActivity>("id" to id)
        }
        val contentView = holder.bind<TextView>(R.id.tv_content)
        val spanText = if (data.replyUserId.isNullOrEmpty()) {
            //这里多余了。什么都没做只是转换成了下类型
            SpanBuilder(data.content?:"")
                    .build()
        } else {
            val content =  String.format("回复%s:%s",data.replyName,data.content)
            val length1 = data.replyName?.length?:0
            SpanBuilder(content)
                    .click(2,2+length1,TextClickableSpan(data.replyUserId))
                    .build()
        }

        val iv_comment_del = holder.bind<ImageView>(R.id.iv_comment_del)
        if(data.userId == userId){
            iv_comment_del.visibility = View.VISIBLE
        }else{
            iv_comment_del.visibility = View.GONE
        }

        iv_comment_del.setOnClickListener({
            deleteAction?.onDelete(data)
        })
        contentView.text = spanText
    }

    private inner class TextClickableSpan(private val id: String?) : ClickableSpan() {

        override fun onClick(widget: View) {
            System.err.println("-view--$widget")

        }
        override fun updateDrawState(ds: TextPaint) {
            ds.color = ContextCompat.getColor(context, R.color.textColor66)
            ds.isUnderlineText = false
        }
    }

    private var deleteAction:DeleteClick?=null
    interface DeleteClick{
        fun onDelete(comment: Comment)
    }

    fun setDeleteClick(action:(comment:Comment)->Unit){
        this.deleteAction = object : TrendCommentAdapter.DeleteClick {
            override fun onDelete(comment: Comment) {
                action(comment)
            }
        }
    }
}