package com.d6.android.app.dialogs

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.d6.android.app.R
import com.d6.android.app.activities.TrendDetailActivity
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.CustomToast
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.dialog_comment_trend_layout.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 评论弹出窗
 */
class CommentTrendDialog : DialogFragment() ,RequestManager,TrendDetailActivity.OnLayoutChangedListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FadeDialog)
    }
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }
    private val id by lazy {
        if (arguments != null && arguments.containsKey("id")) {
            arguments.getString("id")
        } else {
            ""
        }
    }

    private val compositeDisposable = CompositeDisposable()
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout((screenWidth() * 1f).toInt(), wrapContent)
        dialog.window.setGravity(Gravity.BOTTOM)
//        dialog.setOnDismissListener {
//            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(et_content.windowToken, 0)
//        }
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        val ft = manager?.beginTransaction()
        ft?.add(this, tag)
        ft?.commitAllowingStateLoss()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_comment_trend_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_send.setOnClickListener {
            comment()
        }
        et_content.requestFocus()
        et_content.post {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
        }

        et_content.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (et_content.text.length > 0) {
                    tv_send.backgroundResource = R.drawable.shape_15r_orange
                } else {
                    tv_send.backgroundResource = R.drawable.shape_15r_grey
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

//        Handler(Looper.getMainLooper()).postDelayed({
//            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.showSoftInput(et_content, InputMethodManager.SHOW_IMPLICIT)
//        },300)

        if (activity is TrendDetailActivity) {
            (activity as TrendDetailActivity).setOnLayoutChangedListener(this)
        }
    }

    private var dialogListener: OnDialogListener? = null

    fun setDialogListener(l: (p: Int, s: String?) -> Unit) {
        dialogListener = object : OnDialogListener {
            override fun onClick(position: Int, data: String?) {
                l(position, data)
            }
        }
    }
    private var isCommenting = false
    private fun comment() {
        val content = et_content.text.toString().trim()
        if (content.isEmpty()) {
            toast("评论内容不能为空")
            return
        }
//        val replyUid = if (replayUid.isEmpty()) {
//            null
//        } else {
//            replayUid
//        }
        isCommenting = true
        if (activity is BaseActivity) {
            (activity as BaseActivity).dialog()
        }
        Request.addComment(userId, id,content,null).request(this,success = { msg, jsonObject->
            isCommenting = false
            et_content.setText("")
            et_content.clearFocus()
//            replayUid = ""
            toast("评论成功")
            dialogListener?.onClick(0,"")
            dismissAllowingStateLoss()
            showTips(jsonObject,"奖励积分","2")
        }){_,_->isCommenting = false}
    }

    override fun showToast(msg: String) {
        toast(msg)
    }

    override fun onBind(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun dismissDialog() {
        if (activity is BaseActivity) {
            (activity as BaseActivity).dismissDialog()
        }
    }

    override fun onDetach() {
        super.onDetach()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
        if (activity is TrendDetailActivity) {
            (activity as TrendDetailActivity).setOnLayoutChangedListener(null)
        }
    }
    private var lastBottom = 0
    override fun onChanged() {
        val b = ll_root.bottom
        val location = IntArray(2)
        ll_root.getLocationOnScreen(location)
        if (lastBottom == 0) {
            lastBottom = location[1]
            if (screenHeight() - lastBottom >= 200) {//屏幕高度-输入框屏幕位置大于200，认为键盘弹出

            }
        } else {
            if (lastBottom > location[1] && lastBottom-location[1]>=200) {//键盘弹出

            }else if (lastBottom < location[1] && location[1] - lastBottom >= 200) {//键盘收起
                if (isCommenting) {//如果是评论发送导致的。
                    return
                }
                dismissAllowingStateLoss()
            }
        }
    }
}