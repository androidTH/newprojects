package com.d6.android.app.fragments
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.d6.android.app.R
import com.d6.android.app.adapters.FansAdapter
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.models.LoveHeartFans
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.layout_loveheartlist.*

/**
 * 人工推荐
 */
class LoveHeartListQuickFragment : BaseFragment() {

    override fun onFirstVisibleToUser() {

    }

    private val headerView by lazy {
        layoutInflater.inflate(R.layout.item_loveheart,null,false)
    }

    private val mMessages = ArrayList<LoveHeartFans>()
    private val fansAdapter by lazy {
        FansAdapter(mMessages)
    }

    override fun contentViewId(): Int {
        return R.layout.layout_loveheartlist
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
//            mMemberBean = it.getParcelable(ARG_PARAM1)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setData()
        rv_loveheart_fragment.postDelayed(object:Runnable{
            override fun run() {
                rv_loveheart_fragment.setHasFixedSize(true)
                rv_loveheart_fragment.layoutManager = LinearLayoutManager(context)
                rv_loveheart_fragment.adapter = fansAdapter
                fansAdapter.setHeaderView(headerView)
//                VerticalDividerItemDecoration.Builder(context)
//                        .size(size)
//                        .color(ContextCompat.getColor(context, colorRes))
//                        .build()
//                rv_loveheart_fragment.addItemDecoration(divider)
                rv_loveheart_fragment.isNestedScrollingEnabled = false
            }
        },100)
    }

    private fun setData(){

    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                LoveHeartListQuickFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM2, param2)
//                        putParcelable(ARG_PARAM1,param1)
                        putString(ARG_PARAM1,param1)
                    }
                }
    }
}

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"