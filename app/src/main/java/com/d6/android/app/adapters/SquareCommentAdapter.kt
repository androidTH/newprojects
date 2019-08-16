package com.d6.android.app.adapters

import android.support.v4.content.ContextCompat
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.activities.SquareTrendDetailActivity
import com.d6.android.app.activities.UserInfoActivity
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.dialogs.UnKnowInfoDialog
import com.d6.android.app.models.Comment
import com.d6.android.app.utils.CustomLinkMovementMethod
import com.d6.android.app.utils.SpanBuilder
import com.d6.android.app.utils.getLocalUserId
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity

/**
 *
 */
class SquareCommentAdapter(mData:ArrayList<Comment>): HFRecyclerAdapter<Comment>(mData, R.layout.item_list_square_comment) {

    private var mSquareUserId = ""
    private var mNMIndex = 1

    fun setSquareUserId(userId:String,index:Int){
        this.mSquareUserId = userId
        this.mNMIndex = index
    }

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
        var name=""
        if(data.iIsAnonymous==1){
            if(TextUtils.equals(mSquareUserId,data.userId)){
                name = "${data.name}贴主"
            }else {
                if(TextUtils.equals(data.userId, getLocalUserId())){
                    name = "${data.name}${mNMIndex}"
                }else{
                    name = "${data.name}${mNMIndex}"
                    mNMIndex = mNMIndex +1
                }
            }
        }else{
            name = "${data.name}"
        }

        val spanText = if (data.replyUserId.isNullOrEmpty()) {
            val content = String.format("%s: %s",name,data.content)
            val length = name.length
            SpanBuilder(content)
                    .click(0,length,TextClickableSpan(data.userId,data.iIsAnonymous))
                    .build()
        } else {
            var replyName = ""
            if(data.iIsReplyAnonymous==1){
                if(TextUtils.equals(data.replyUserId, mSquareUserId)){
                    replyName = "${data.replyName}贴主"
                }else{
                    if(TextUtils.equals(data.replyUserId, getLocalUserId())){
                        replyName = "${data.replyName}${mNMIndex}"
                    }else{
                        replyName = "${data.replyName}${mNMIndex}"
                        mNMIndex = mNMIndex + 1
                    }
                }
            }else{
                replyName = "${data.replyName}"
            }

            val content =  String.format("%s回复%s: %s",name,replyName,data.content)

            val length = name.length
            val length1 = replyName.length
            SpanBuilder(content)
                    .click(0,length,TextClickableSpan(data.userId,data.iIsAnonymous))
                    .click(length+2,length+2+length1,TextClickableSpan(data.replyUserId,data.iIsReplyAnonymous))
                    .build()
        }


        contentView.text = spanText
    }
    private var textClickedListener: CustomLinkMovementMethod.TextClickedListener? = null

    fun setOnCommentClick(l:()->Unit) {
        textClickedListener = CustomLinkMovementMethod.TextClickedListener { l() }
    }

    private inner class TextClickableSpan(private val id: String?,private val iIsAnonymous:Int?) : ClickableSpan() {

        override fun onClick(widget: View) {
            val userId = id?:""
            if(iIsAnonymous==1){
                var mUnknowDialog = UnKnowInfoDialog()
                mUnknowDialog.arguments = bundleOf("otheruserId" to "${id}")
                mUnknowDialog.show((context as BaseActivity).supportFragmentManager,"unknowDialog")
            }else{
                context.startActivity<UserInfoActivity>("id" to userId)
            }
        }
        override fun updateDrawState(ds: TextPaint) {
            ds.color = ContextCompat.getColor(context,R.color.textColor66)
            ds.isUnderlineText = false
        }
    }

}