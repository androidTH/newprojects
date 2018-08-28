package com.d6.android.app.fragments

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import com.d6.android.app.R
import com.d6.android.app.activities.MyDateActivity
import com.d6.android.app.activities.UserInfoActivity
import com.d6.android.app.adapters.DateCardAdapter
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.dialogs.DateErrorDialog
import com.d6.android.app.dialogs.DateSendedDialog
import com.d6.android.app.dialogs.FilterCityDialog
import com.d6.android.app.dialogs.FilterDateTypeDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.DateBean
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.gyf.barlibrary.ImmersionBar
import com.lin.cardlib.CardSetting
import com.lin.cardlib.CardLayoutManager
import com.lin.cardlib.utils.ReItemTouchHelper
import com.lin.cardlib.CardTouchHelperCallback
import com.lin.cardlib.OnSwipeCardListener
import kotlinx.android.synthetic.main.fragment_date.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.support.v4.startActivity


/**
 * 约会
 */
class DateFragment : BaseFragment() {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val sex by lazy {
        SPUtils.instance().getString(Const.User.USER_SEX)
    }

    private var city: String? = null
    private var outCity: String? = null
    private var type: Int = 0

    private val mDates = ArrayList<DateBean>()

    override fun contentViewId() = R.layout.fragment_date
    private val immersionBar by lazy {
        ImmersionBar.with(this)
    }

    override fun onFirstVisibleToUser() {
        immersionBar
                .fitsSystemWindows(true)
                .statusBarDarkFont(true)
                .init()

        val setting = CardSetting()
        val helperCallback = CardTouchHelperCallback(mRecyclerView, mDates, setting)
        val mReItemTouchHelper = ReItemTouchHelper(helperCallback)
        val layoutManager = CardLayoutManager(mReItemTouchHelper, setting)
        mRecyclerView.layoutManager = layoutManager
        val cardAdapter = DateCardAdapter(mDates)
        mRecyclerView.adapter = cardAdapter

        cardAdapter.setOnItemClickListener { view, position ->
            val dateBean = mDates[position]
            startActivity<UserInfoActivity>("id" to dateBean.accountId)
        }

        setting.setSwipeListener(object : OnSwipeCardListener<DateBean> {
            override fun onSwiping(viewHolder: RecyclerView.ViewHolder?, dx: Float, dy: Float, direction: Int) {

            }

            override fun onSwipedOut(viewHolder: RecyclerView.ViewHolder?, t: DateBean?, direction: Int) {
                getNext()

            }

            override fun onSwipedClear() {
            }

        })
        headView.setOnClickListener {
            getAuthState()
        }
        tv_my_date.setOnClickListener {
            getAuthState()
        }
        tv_city.setOnClickListener {
            val filterCityDialog = FilterCityDialog()
            filterCityDialog.hidleCancel(TextUtils.isEmpty(city) && TextUtils.isEmpty(outCity))
            filterCityDialog.show(childFragmentManager, "fcd")
            filterCityDialog.setDialogListener { p, s ->
                if (p == 1) {
                    city = s
                    outCity = null
                } else if (p == 2) {
                    city = null
                    outCity = s
                } else if (p == -2) {//取消选择
                    city = null
                    outCity = null
                }
                tv_city.text = s
                getData(1)
            }
        }
        tv_type.setOnClickListener {
            val filterDateTypeDialog = FilterDateTypeDialog()
            filterDateTypeDialog.show(childFragmentManager, "ftd")
            filterDateTypeDialog.setDialogListener { p, s ->
                type = p
                tv_type.text = s
                getData(1)
            }
        }

        btn_like.setOnClickListener {
            tv_tip.gone()
            if (mDates.isNotEmpty()) {
//                activity?.isVipSilver {
                val date = mDates[0]
                sysErr("--->$date")
                showDialog()
                sendDateRequest(date)
//                }
            } else {
                tv_tip.visible()
            }
        }

        fb_unlike.setOnClickListener {
            //            val dateErrorDialog = DateErrorDialog()
//            dateErrorDialog.show(childFragmentManager, "d")
            tv_tip.gone()
//            tv_main_card_bg_im_id.gone()
//            tv_main_card_Bg_tv_id.gone()
            if (mDates.isNotEmpty()) {
                val date = mDates[0]
                mDates.remove(date)
                mRecyclerView.adapter.notifyDataSetChanged()
                getNext()
            } else {
                tv_tip.visible()
//                tv_main_card_bg_im_id.visible()
//                tv_main_card_Bg_tv_id.visible()
            }
        }

        showDialog()
        getData()
    }

    override fun onResume() {
        super.onResume()
        val head = SPUtils.instance().getString(Const.User.USER_HEAD)
        headView.setImageURI(head)
    }

    fun getData(type: Int = 0) {
        if (mDates.size == 0) {
            tv_main_card_bg_im_id.visible()
            tv_main_card_Bg_tv_id.visible()
            fb_unlike.gone()
            btn_like.gone()
        }
        Request.getHomeDateList(userId, sex, type, city, outCity).request(this) { _, data ->
            if (type == 1) {
                mDates.clear()
            }
            data?.let {
                mDates.addAll(it)
            }
            mRecyclerView.adapter.notifyDataSetChanged()
            if (mDates.isEmpty()) {
                tv_tip.gone()
                tv_main_card_bg_im_id.visible()
                tv_main_card_Bg_tv_id.visible()
                fb_unlike.gone()
                btn_like.gone()
            } else {
                tv_tip.gone()
                tv_main_card_bg_im_id.gone()
                tv_main_card_Bg_tv_id.gone()
                fb_unlike.visible()
                btn_like.visible()
            }
        }
    }


    private fun sendDateRequest(dateBean: DateBean) {
        Request.dateUser(userId, dateBean.accountId).request(this, success = { _, data ->
            val dateSendedDialog = DateSendedDialog()
            dateSendedDialog.arguments = bundleOf("data" to dateBean)
            dateSendedDialog.show(childFragmentManager, "d")
            mDates.remove(dateBean)
            mRecyclerView.adapter.notifyDataSetChanged()
            //
            getNext()
        }) { code, msg ->
            val dateErrorDialog = DateErrorDialog()
            val images = dateBean.userpics?.split(",")
            val img = if (images != null && images.isNotEmpty()) {
                images[0]
            } else {
                dateBean.picUrl ?: ""
            }
            dateErrorDialog.arguments = bundleOf("msg" to msg, "img" to img)
            dateErrorDialog.show(childFragmentManager, "d")
        }
    }

    fun getNext() {
        if (mDates.size <= 2) {
            getData()
        }
    }

    private fun getAuthState() {
        showDialog()
        Request.getAuthState(userId).request(this) { _, data ->
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

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            immersionBar.init()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }
}