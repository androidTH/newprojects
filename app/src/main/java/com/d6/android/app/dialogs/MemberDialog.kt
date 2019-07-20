package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.activities.MyDateActivity
import com.d6.android.app.adapters.MemberDescHolder
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.models.MemberDesc
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.convenientbanner.holder.CBViewHolderCreator
import com.d6.android.app.widget.convenientbanner.listener.OnPageChangeListener
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.member_dialog.*
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 会员弹窗
 */
class MemberDialog : DialogFragment(),RequestManager {

    var mMemberDesc = ArrayList<MemberDesc>()

    private val compositeDisposable by lazy {
        CompositeDisposable()
    }
    override fun showToast(msg: String) {
        toast(msg)
    }

    override fun dismissDialog() {
        if (context is BaseActivity) {
            (context as BaseActivity).dismissDialog()
        }
    }

    override fun onBind(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FadeDialog)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout((screenWidth() * 0.75f).toInt() + dip(30), wrapContent)
        dialog.window.setGravity(Gravity.CENTER)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        val ft = manager?.beginTransaction()
        ft?.add(this, tag)
        ft?.commitAllowingStateLoss()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.member_dialog, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var from  = arguments.getString(Const.NO_VIP_FROM_TYPE)

        if(TextUtils.equals("mine",from)){
            tv_member.text = "会员有价 情缘无价"
        }else{
            tv_member.text = "成为会员后即可使用该功能"
        }
        mMemberDesc.add(MemberDesc("人工精准匹配约会，超高成功率","专属客服匹配","res:///"+R.mipmap.windows_tz1))
        mMemberDesc.add(MemberDesc("直接开聊拒绝骚扰","一对一私聊",
                "res:///"+R.mipmap.windows_tz2))
        mMemberDesc.add(MemberDesc("多金？有颜？总有一个是你的菜","优质会员任你挑",
                "res:///"+R.mipmap.windows_tz3))
        mMemberDesc.add(MemberDesc("提供交友、线上群聊、线下聚会、酒店旅行折扣","私人定制服务",
                "res:///"+R.mipmap.windows_tz4))

        member_banner.setPages(
                object : CBViewHolderCreator {
                    override fun createHolder(itemView: View): MemberDescHolder {
                        return MemberDescHolder(itemView)
                    }

                    override fun getLayoutId(): Int {
                        return R.layout.item_member_banner
                    }
                },mMemberDesc)

        member_banner.setOnPageChangeListener(object: OnPageChangeListener {
            override fun onPageSelected(index: Int) {
                when(index){
                    0-> {
                        if(tv_numone_member!=null){
                            tv_numone_member.isEnabled = false
                        }
                        if(tv_numtwo_member!=null){
                            tv_numtwo_member.isEnabled = true
                        }

                        if(tv_numthree_member!=null){
                            tv_numthree_member.isEnabled = true
                        }

                        if(tv_numfour_member!=null){
                            tv_numfour_member.isEnabled = true
                        }
                    }
                    1->{
                        if(tv_numone_member!=null){
                            tv_numone_member.isEnabled = true
                        }
                        if(tv_numtwo_member!=null){
                            tv_numtwo_member.isEnabled = false
                        }

                        if(tv_numthree_member!=null){
                            tv_numthree_member.isEnabled = true
                        }

                        if(tv_numfour_member!=null){
                            tv_numfour_member.isEnabled = true
                        }
                    }
                    2->{

                        if(tv_numone_member!=null){
                            tv_numone_member.isEnabled = true
                        }
                        if(tv_numtwo_member!=null){
                            tv_numtwo_member.isEnabled = true
                        }

                        if(tv_numthree_member!=null){
                            tv_numthree_member.isEnabled = false
                        }

                        if(tv_numfour_member!=null){
                            tv_numfour_member.isEnabled = true
                        }
                    }
                    3->{

                        if(tv_numone_member!=null){
                            tv_numone_member.isEnabled = true
                        }
                        if(tv_numtwo_member!=null){
                            tv_numtwo_member.isEnabled = true
                        }

                        if(tv_numthree_member!=null){
                            tv_numthree_member.isEnabled = true
                        }

                        if(tv_numfour_member!=null){
                            tv_numfour_member.isEnabled = false
                        }
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            }
        })

        tv_know.setOnClickListener {
               isBaseActivity {
                   it.pushCustomerMessage(it, getLocalUserId(),1,"",next = {
                       chatService(it)
                       dismissAllowingStateLoss()
                   })
               }
        }
    }

    override fun onResume() {
        super.onResume()
        member_banner.startTurning()
    }

    override fun onStop() {
        super.onStop()
        member_banner.startTurning()
    }

    private fun getAuthState() {
        if (context is BaseActivity) {
            (context as BaseActivity).dialog(canCancel = false)
        }
        val userId = SPUtils.instance().getString(Const.User.USER_ID)
        Request.getAuthState(userId).request(this){ _, data->
//            if (data != null) {
//                val wanshanziliao = data.optDouble("wanshanziliao")
//                if (wanshanziliao < 8) {//资料完善程度大于=80%
//                    startActivity<DateAuthStateActivity>()
//                    return@request
//                }
//                val lianxifangshi = data.optInt("lianxifangshi")
//                if (lianxifangshi == 0) {
//                    startActivity<DateAuthStateActivity>()
//                    return@request
//                }
//
//                val qurenzheng = data.optInt("qurenzheng")
//                if (qurenzheng == 0) {
//                    startActivity<DateAuthStateActivity>()
//                    return@request
//                }
//
//                startActivity<MyDateActivity>()
//
//            } else {
//                startActivity<DateAuthStateActivity>()
//            }

            if (data != null) {
                val wanshanziliao = data.optDouble("wanshanziliao")
                if (wanshanziliao < 8) {//资料完善程度大于=80%
                    startActivity<MyDateActivity>("whetherOrNotToBeCertified" to "0")
                    return@request
                }
                val lianxifangshi = data.optInt("lianxifangshi")
                if (lianxifangshi == 0) {
                    startActivity<MyDateActivity>("whetherOrNotToBeCertified" to "0")
                    return@request
                }

                val qurenzheng = data.optInt("qurenzheng")
                if (qurenzheng == 0) {
                    startActivity<MyDateActivity>("whetherOrNotToBeCertified" to "0")
                    return@request
                }

                startActivity<MyDateActivity>("whetherOrNotToBeCertified" to "1")

            } else {//startActivity<DateAuthStateActivity>
                startActivity<MyDateActivity>("whetherOrNotToBeCertified" to "0")
            }


        }
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private var dialogListener: OnDialogListener? = null

    fun setDialogListener(l: (p: Int, s: String?) -> Unit) {
        dialogListener = object : OnDialogListener {
            override fun onClick(position: Int, data: String?) {
                l(position, data)
            }
        }
    }
}