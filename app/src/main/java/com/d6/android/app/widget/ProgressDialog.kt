package com.d6.android.app.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.d6.android.app.R
import org.jetbrains.anko.find

/**
 *
 */
class ProgressDialog : Dialog {
    private var mMessage: CharSequence = "加载中..."
    var mProgressBar :ProgressBar ? = null
    var mMessageView :TextView ? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, theme: Int) : super(context, theme)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.progress_dialog)
        mProgressBar = find(R.id.mProgressBar)
        mMessageView = find(R.id.mMessageView)
        setMessage(mMessage)
    }

    fun setIndeterminate(indeterminate: Boolean) {
        if (mProgressBar != null) {
            mProgressBar?.isIndeterminate = indeterminate
        }
    }

    fun setMessage(message: CharSequence,visibility:Boolean = true) {

        if (mMessageView == null) {
            mMessage = message
        } else {
            mMessageView?.text = message
        }

        if(visibility){
            mMessageView?.visibility=View.VISIBLE
        }else{
            mMessageView?.visibility=View.GONE
        }
    }
}