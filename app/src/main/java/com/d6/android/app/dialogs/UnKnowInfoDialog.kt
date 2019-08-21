package com.d6.android.app.dialogs

import android.graphics.drawable.Drawable
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
import com.d6.android.app.adapters.CardUnKnowTagAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.models.UserData
import com.d6.android.app.models.UserUnKnowTag
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_unknow_info.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 匿名弹窗
 */
class UnKnowInfoDialog : DialogFragment(), RequestManager {

    private var userJson = SPUtils.instance().getString(Const.USERINFO)
    private var mUserInfo = GsonHelper.getGson().fromJson(userJson,UserData::class.java)

    private val mTags = ArrayList<UserUnKnowTag>()

    private val userTagAdapter by lazy {
        CardUnKnowTagAdapter(mTags)
    }

    private var otherUserId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FadeDialog)
    }

    private val compositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout((screenWidth() * 0.7f).toInt() + dip(30), wrapContent)
        dialog.window.setGravity(Gravity.CENTER)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        val ft = manager?.beginTransaction()
        ft?.add(this, tag)
        ft?.commitAllowingStateLoss()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_unknow_info, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        otherUserId = if(arguments!=null){
            arguments.getString("otheruserId")
        }else{
            ""
        }

        tv_dialogunknow_start.setOnClickListener {
            dismissAllowingStateLoss()
        }

        unknow_headview.setImageURI("res:///"+R.mipmap.nimingtouxiang_big)

        rv_dialog_unknow_tags.setHasFixedSize(true)
        rv_dialog_unknow_tags.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        rv_dialog_unknow_tags.isNestedScrollingEnabled = false
        rv_dialog_unknow_tags.adapter = userTagAdapter
        getUserInfo()
    }

    private fun getUserInfo() {
        isBaseActivity {
            Request.getUserInfo(getLocalUserId(),otherUserId).request(it, success = { _, data ->
                data?.let {
                    if (tv_unknow_sex != null) {
                        tv_unknow_sex.isSelected = TextUtils.equals("0", it.sex)
                        it.age?.let {
                            if (it.toInt() <= 0) {
                                tv_unknow_sex.text = ""
                            } else {
                                tv_unknow_sex.text = it
                            }
                        }
                    }
                    var drawable: Drawable? = getLevelDrawable(it.userclassesid.toString(),context)
                    //27入门 28中级  29优质
                    if(drawable!=null){
                        tv_vip.backgroundDrawable = getLevelDrawable(it.userclassesid.toString(),context)
                    }else{
                        tv_vip.visibility = View.GONE
                    }

                    mTags.clear()
                    if (!it.constellation.isNullOrEmpty()) {
                        mTags.add(UserUnKnowTag("星座"," ${it.constellation}",R.mipmap.boy_constellation_icon))
                    }else{
                        mTags.add(UserUnKnowTag("星座","-",R.mipmap.boy_constellation_icon))
                    }

                    if (!it.city.isNullOrEmpty()) {
                        mTags.add(UserUnKnowTag("地区","${it.city}",R.mipmap.boy_area_icon))
                    }else{
                        mTags.add(UserUnKnowTag("地区","-",R.mipmap.boy_area_icon))
                    }

                    if (!it.job.isNullOrEmpty()) {
                        mTags.add(UserUnKnowTag("职业", "${it.job}",R.mipmap.boy_profession_icon))
                    }else{
                        mTags.add(UserUnKnowTag("职业", "-",R.mipmap.boy_profession_icon))
                    }

                    if (!it.zuojia.isNullOrEmpty()) {
                        mTags.add(UserUnKnowTag("座驾","${it.zuojia}",R.mipmap.boy_car_icon))
                    }else{
                        mTags.add(UserUnKnowTag("座驾","-",R.mipmap.boy_car_icon))
                    }


                    if (!it.hobbit.isNullOrEmpty()) {
                        var mHobbies = it.hobbit?.replace("#", ",")?.split(",")
                        var sb = StringBuffer()
                        if (mHobbies != null) {
                            for (str in mHobbies) {
//                            mTags.add(UserTag(str, R.drawable.shape_tag_bg_6))
                                sb.append("${str} ")
                            }
                            mTags.add(UserUnKnowTag("爱好",sb.toString(),R.mipmap.boy_hobby_icon))
                        }
                    }else{
                        mTags.add(UserUnKnowTag("爱好","-",R.mipmap.boy_hobby_icon))
                    }
                    userTagAdapter.notifyDataSetChanged()
                }
            })
        }
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
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

    override fun showToast(msg: String) {
        toast(msg)
    }

    override fun onBind(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun dismissDialog() {
        (context as BaseActivity).dismissDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}