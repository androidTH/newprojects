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
import com.d6.android.app.models.MemberTeQuan
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

    private var mListTQ = ArrayList<MemberTeQuan>()

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
        mMemberBean?.let {
            mListTQ = it.lstMembers as ArrayList<MemberTeQuan>
        }
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