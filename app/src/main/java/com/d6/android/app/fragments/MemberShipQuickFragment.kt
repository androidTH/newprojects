package com.d6.android.app.fragments
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.TeQuanQuickAdapter
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.models.MemberBean
import com.d6.android.app.models.MemberServiceBean
import com.d6.android.app.utils.AppUtils
import com.d6.android.app.widget.GridItemDecoration
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.layout_memebership_one.*

/**
 * 人工推荐
 */
class MemberShipQuickFragment : BaseFragment() {

    override fun onFirstVisibleToUser() {

    }

    private var mMemberBean:MemberBean?=null

    private var mListTQ = ArrayList<MemberServiceBean>()

    private val mTeQuanQuickAdapter by lazy{
        TeQuanQuickAdapter(mListTQ)
    }

    override fun contentViewId(): Int {
        return R.layout.layout_memebership_one
    }

    override fun showToast(msg: String) {
        super.showToast(msg)
    }

    override fun dismissDialog() {
        super.dismissDialog()
    }

    override fun onBind(disposable: Disposable) {
        super.onBind(disposable)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mMemberBean = it.getParcelable(ARG_PARAM1)
//            iLookType = it.getString(ARG_PARAM1)
//            sPlace = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mMemberBean?.let {
            tv_membership_viptq.text = "${it.classesname} ${it.sTitle}"

            if (TextUtils.isEmpty(it.sServiceArea)) {
                tv_endtime.setVisibility(View.GONE)
            } else {
                tv_endtime.setText(it.sServiceArea)
            }
//           if(it.iRecommendCount==0){
//               tv_ztnums.text = "无直推"
//           }else{
//               tv_ztnums.text = "直推次数:${it.iRecommendCount}"
//           }
            tv_ztnums.text = "有效期: ${it.sEnableDateDesc}"
        }
        setData()
        rv_openmember_fragment.setHasFixedSize(true)
        rv_openmember_fragment.layoutManager = GridLayoutManager(context, 3) as RecyclerView.LayoutManager?
        rv_openmember_fragment.adapter = mTeQuanQuickAdapter
        var divider = GridItemDecoration.Builder(context)
                .setHorizontalSpan(R.dimen.margin_1)
                .setVerticalSpan(R.dimen.margin_1)
                .setColorResource(R.color.color_F5F5F5)
                .setShowLastLine(false)
                .setShowVerticalLine(false)
                .build()
        rv_openmember_fragment.addItemDecoration(divider)
        rv_openmember_fragment.isNestedScrollingEnabled = false
    }

    private fun setData(){
        var mService = MemberServiceBean("0")
        mService.mResId = R.mipmap.vippage_tequan_sf
        mService.mClassName = "会员身份"
        mService.mClassDesc = "显示“会员”专属身份随处可见"
        mService.mClassTag = "APP"
        mService.mClassType = 1


        var mService1 = MemberServiceBean("0")
        mService1.mResId = R.mipmap.vippage_tequan_dt
        mService1.mClassName = "发布动态"
        mService1.mClassDesc = "可发布动态与优质会员互动"
        mService1.mClassTag = "APP"
        mService1.mClassType = 1

        var mService2 = MemberServiceBean("0")
        mService2.mResId = R.mipmap.vippage_tequan_yh
        mService2.mClassName = "自主约会"
        mService2.mClassDesc = "发起约会或申请赴约拒绝低效"
        mService2.mClassTag = "APP"
        mService2.mClassType = 1


        var mService3 = MemberServiceBean("0")
        mService3.mResId = R.mipmap.vippage_tequan_wxq
        mService3.mClassName = "微信群"
        mService3.mClassDesc = "包含地区群、字母、游戏、健身等特色群"
        mService3.mClassTag = "微信"
        mService3.mClassType = 2


        var mService4 = MemberServiceBean("0")
        mService4.mResId = R.mipmap.vippage_tequan_yzhy
        mService4.mClassName = "优质会员群"
        mService4.mClassDesc = "群内女生颜值高，男生均为钻石及以上会员"
        mService4.mClassTag = "微信"
        mService4.mClassType = 2


        var mService5 = MemberServiceBean("0")
        mService5.mResId = R.mipmap.vippage_tequan_pyq
        mService5.mClassName = "朋友圈广告"
        mService5.mClassDesc = "赠送朋友圈广告广而告之"
        mService5.mClassTag = "微信"
        mService5.mClassType = 2

        var mService6 = MemberServiceBean("0")
        mService6.mResId = R.mipmap.vippage_tequan_hy
        mService6.mClassName = "会员入档"
        mService6.mClassDesc = "制作会员卡片，上传至官网、APP，并入档"
        mService6.mClassTag = "人工"
        mService6.mClassType = 3


        var mService7 = MemberServiceBean("0")
        mService7.mResId = R.mipmap.vippage_tequan_lm
        mService7.mClassName = "撩妹培训"
        mService7.mClassDesc = "可参加PUA撩妹教学免费培训"
        mService7.mClassTag = "人工"
        mService7.mClassType = 3

        var mService8 = MemberServiceBean("0")
        mService8.mResId = R.mipmap.vippage_tequan_xxhd
        mService8.mClassName = "线下活动"
        mService8.mClassDesc = "可参与各类线下娱乐活动"
        mService8.mClassTag = "人工"
        mService8.mClassType = 3

        mListTQ.add(mService)
        mListTQ.add(mService1)
        mListTQ.add(mService2)
        mListTQ.add(mService3)
        mListTQ.add(mService4)
        mListTQ.add(mService5)
        mListTQ.add(mService6)
        mListTQ.add(mService7)
        mListTQ.add(mService8)
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: MemberBean, param2: String) =
                MemberShipQuickFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM2, param2)
                        putParcelable(ARG_PARAM1,param1)
                    }
                }
    }
}

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"