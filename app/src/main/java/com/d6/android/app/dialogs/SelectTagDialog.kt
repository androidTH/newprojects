package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.adapters.TagAdapter
import com.d6.android.app.extentions.request
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.models.SquareTag
import com.d6.android.app.net.Request
import com.d6.android.app.utils.OnDialogListener
import com.d6.android.app.utils.screenWidth
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_select_tag_layout.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 选择板块标识弹窗
 */
class SelectTagDialog : DialogFragment() ,RequestManager{
    override fun showToast(msg: String) {
        toast(msg)
    }

    override fun dismissDialog() {
    }

    override fun onBind(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    private val mTags = ArrayList<SquareTag>()
    private val tagAdapter by lazy {
        TagAdapter(mTags)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FadeDialog)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout(screenWidth(), matchParent)
        dialog.setCanceledOnTouchOutside(true)
    }
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_select_tag_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_back.setOnClickListener {
            dismissAllowingStateLoss()
        }
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerView.adapter = tagAdapter

        tagAdapter.setOnItemClickListener { _, position ->
            dialogListener?.onClick(mTags[position].id,mTags[position].content)
            dismissAllowingStateLoss()
        }

        getTags()
    }

    private fun getTags() {
        Request.getSquareTags().request(this){_,data->
            mTags.clear()
            data?.let {
                it.forEach {
                    if (!TextUtils.equals("全部", it.content)) {
                        mTags.add(it)
                    }
                }
            }
            tagAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    private var dialogListener: OnDialogListener? = null

    fun setDialogListener(l: (p: String?, s: String?) -> Unit) {
        dialogListener = object : OnDialogListener {
            override fun onClick(id: String?, data: String?) {
                l(id, data)
            }
        }
    }

    interface OnDialogListener {
        fun onClick(id: String?, data: String?)
    }
}