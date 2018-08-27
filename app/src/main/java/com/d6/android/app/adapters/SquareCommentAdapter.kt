package com.d6.android.app.adapters

import android.support.v4.content.ContextCompat
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.activities.SquareTrendDetailActivity
import com.d6.android.app.activities.UserInfoActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.Comment
import com.d6.android.app.utils.CustomLinkMovementMethod
import com.d6.android.app.utils.SpanBuilder
import org.jetbrains.anko.startActivity

/**
 *
 */
class SquareCommentAdapter(mData:ArrayList<Comment>): HFRecyclerAdapter<Comment>(mData, R.layout.item_list_square_comment) {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val holder = super.onCreateViewHolder(parent, viewType)
        val contentView = holder.bind<TextView>(R.id.tv_content)
        val c =  CustomLinkMovementMethod()
        c.setOnTextClickListener {
            textClickedListener?.onTextClicked()
        }
        contentView.movementMethod = c
        return holder
    }
    override fun onBind(holder: ViewHolder, position: Int, data: Comment) {
        val contentView = holder.bind<TextView>(R.id.tv_content)
        val spanText = if (data.replyUserId.isNullOrEmpty()) {
            val content = String.format("%s:%s",data.name,data.content)
            val length = data.name?.length?:0
            SpanBuilder(content)
                    .click(0,length,TextClickableSpan(data.userId))
                    .build()
        } else {
            val content =  String.format("%s回复%s:%s",data.name,data.replyName,data.content)
            val length = data.name?.length?:0
            val length1 = data.replyName?.length?:0
            SpanBuilder(content)
                    .click(0,length,TextClickableSpan(data.userId))
                    .click(length+2,length+2+length1,TextClickableSpan(data.replyUserId))
                    .build()
        }
        contentView.text = spanText
    }
    private var textClickedListener: CustomLinkMovementMethod.TextClickedListener? = null
    fun setOnCommentClick(l:()->Unit) {
        textClickedListener = CustomLinkMovementMethod.TextClickedListener { l() }
    }

    private inner class TextClickableSpan(private val id: String?) : ClickableSpan() {

        override fun onClick(widget: View) {
            val userId = id?:""
            context.startActivity<UserInfoActivity>("id" to userId)
        }
        override fun updateDrawState(ds: TextPaint) {
            ds.color = ContextCompat.getColor(context,R.color.color_369)
            ds.isUnderlineText = false
        }
    }

}