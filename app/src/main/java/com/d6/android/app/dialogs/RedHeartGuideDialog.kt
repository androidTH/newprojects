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
import com.d6.android.app.adapters.RedHeartGuideHolder
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.models.MemberDesc
import com.d6.android.app.utils.*
import com.d6.android.app.widget.convenientbanner.holder.CBViewHolderCreator
import com.d6.android.app.widget.convenientbanner.listener.OnPageChangeListener
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.redheartguide_dialog.*
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 会员弹窗
 */
class RedHeartGuideDialog : DialogFragment(),RequestManager {

    var mMemberDesc = ArrayList<MemberDesc>()

    private val mUserSex by lazy {
        SPUtils.instance().getString(Const.User.USER_SEX)
    }

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
            inflater?.inflate(R.layout.redheartguide_dialog, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(TextUtils.equals("0",mUserSex)){
            mMemberDesc.add(MemberDesc("","资料完成度越高，收到的 [img src=redheart_small/] 越多","res:///"+R.mipmap.poppic_d,R.mipmap.poppic_d))
            mMemberDesc.add(MemberDesc("","送的越多回复你的概率越高",
                    "res:///"+R.mipmap.poppic_e,R.mipmap.poppic_e))
            mMemberDesc.add(MemberDesc("","首次赠送10颗 [img src=redheart_small/] 用完可充值",
                    "res:///"+R.mipmap.poppic_f,R.mipmap.poppic_f))
            setShowTips(0, "资料完成度越高，收到的 [img src=redheart_small/] 越多")
        }else{
            mMemberDesc.add(MemberDesc("","相互喜欢即可解锁聊天","res:///"+R.mipmap.poppic_a,R.mipmap.poppic_a))
            mMemberDesc.add(MemberDesc("","收到的 [img src=redheart_small/] 可前往[钱包]中提现",
                    "res:///"+R.mipmap.poppic_b,R.mipmap.poppic_b))
            mMemberDesc.add(MemberDesc("","首次赠送10颗 [img src=redheart_small/] 用完可充值",
                    "res:///"+R.mipmap.poppic_c,R.mipmap.poppic_c))

            setShowTips(0, "相互喜欢即可解锁聊天")
        }

        member_banner.setPages(
                object : CBViewHolderCreator {
                    override fun createHolder(itemView: View): RedHeartGuideHolder {
                        return RedHeartGuideHolder(itemView)
                    }

                    override fun getLayoutId(): Int {
                        return R.layout.item_redheart_banner
                    }
                },mMemberDesc)

        member_banner.setOnPageChangeListener(object: OnPageChangeListener {
            override fun onPageSelected(index: Int) {
                var title = mMemberDesc.get(index).title
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
                        setShowTips(0, title)
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
                        setShowTips(1,title)
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

                        setShowTips(2,title)
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            }
        })

        tv_know.setOnClickListener {
            dismissAllowingStateLoss()
//               isBaseActivity {
//                   it.pushCustomerMessage(it, getLocalUserId(),1,"",next = {
//                       chatService(it)
//                       dismissAllowingStateLoss()
//                   })
//               }
        }
    }

    private fun setShowTips(index:Int,content:String){
        if(index==0){
            tv_banner_desc.text = "${content}"
        }else if(index==1){
            tv_banner_desc.text = "${content}"
        }else if(index==2){
            tv_banner_desc.text = "${content}"
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