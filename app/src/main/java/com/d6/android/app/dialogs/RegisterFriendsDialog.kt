package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.adapters.RegisterUserInfoQuickAdapter
import com.d6.android.app.extentions.request
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_register_friends.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 约会发送出错
 */
class RegisterFriendsDialog : DialogFragment(),RequestManager {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FadeDialog)
    }

    private val compositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout((screenWidth() * 0.8f).toInt()+dip(30), wrapContent)
        dialog.window.setGravity(Gravity.CENTER)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        val ft = manager?.beginTransaction()
        ft?.add(this, tag)
        ft?.commitAllowingStateLoss()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_register_friends, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycler()
        if (arguments != null) {
           var userId = arguments.getString("userId")
           getData(userId)
        }

        tv_close.setOnClickListener {
            dismissAllowingStateLoss()
        }

        mData.add("注册D6")
        mData.add("成为会员：App会员(1月）")
        mData.add("续费会员：App会员(1月）")
        mData.add("白银会员")
        mRegisterUserInfoAdapter?.notifyDataSetChanged()
    }

    var mData = ArrayList<String>()
    private var mRegisterUserInfoAdapter: RegisterUserInfoQuickAdapter?= RegisterUserInfoQuickAdapter(mData)

    private fun initRecycler(){
        rv_time.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        rv_time.adapter = mRegisterUserInfoAdapter
    }

    private fun getData(userId:String) {
        Request.getUserInfo(getLocalUserId(), userId).request(this, success = { _, data ->
            data?.let {
                user_headView.setImageURI(it.picUrl)
                tv_name.text = it.name
                tv_sex.isSelected = TextUtils.equals("0", it.sex)
                it.age?.let {
                    if (it.toInt() <= 0) {
                        tv_sex.text = ""
                    } else {
                        tv_sex.text = "${it}"
                    }
                }
                var drawable = getLevelDrawable("${it.userclassesid}",context)
                tv_vip.backgroundDrawable = drawable
            }
        })
    }

    private var dialogListener: OnDialogListener? = null

    fun setDialogListener(l: (p: Int, s: String?) -> Unit) {
        dialogListener = object : OnDialogListener {
            override fun onClick(position: Int, data: String?) {
                l(position, data)
            }
        }
    }

    override fun showToast(msg: String) {
        toast(msg)
    }

    override fun onBind(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun dismissDialog() {

    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}