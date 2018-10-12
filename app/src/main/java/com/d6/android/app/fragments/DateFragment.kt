package com.d6.android.app.fragments

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
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
import com.d6.android.app.widget.gallery.AnimManager
import com.d6.android.app.widget.gallery.GalleryRecyclerView
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
class DateFragment : BaseFragment(), GalleryRecyclerView.OnItemClickListener {
    override fun onItemClick(view: View?, position: Int) {
        val dateBean = mDates[position]
        startActivity<UserInfoActivity>("id" to dateBean.accountId)
    }

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val sex by lazy {
        SPUtils.instance().getString(Const.User.USER_SEX)
    }

    private var city: String? = null
    private var outCity: String? = null
    private var type: Int = 0
    private var cityType: Int = -2

    private val mDates = ArrayList<DateBean>()

    override fun contentViewId() = R.layout.fragment_date

    override fun onFirstVisibleToUser() {
        immersionBar.statusBarColor(R.color.colorPrimaryDark).init()
        mRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val cardAdapter = DateCardAdapter(mDates)
        mRecyclerView.adapter = cardAdapter
        mRecyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    var position = mRecyclerView.scrolledPosition;
                    if ((mDates.size-position) <= 2) {
                        getData()
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        mRecyclerView
                // 设置滑动速度（像素/s）
                .initFlingSpeed(9000)
                // 设置页边距和左右图片的可见宽度，单位dp
                .initPageParams(-10, 40)
                // 设置切换动画的参数因子
                .setAnimFactor(0.1f)
                // 设置切换动画类型，目前有AnimManager.ANIM_BOTTOM_TO_TOP和目前有AnimManager.ANIM_TOP_TO_BOTTOM
                .setAnimType(AnimManager.ANIM_BOTTOM_TO_TOP)
                // 设置点击事件
                .setOnItemClickListener(this)
                // 设置自动播放
                .autoPlay(false)
                // 设置自动播放间隔时间 ms
                .intervalTime(2000)
                // 设置初始化的位置
                .initPosition(0)
                // 在设置完成之后，必须调用setUp()方法
                .setUp()

        headView.setOnClickListener {
            getAuthState()
        }

        tv_my_date.setOnClickListener {
            getAuthState()
        }

        tv_city.setOnClickListener {
            val filterCityDialog = FilterCityDialog()
            filterCityDialog.hidleCancel(TextUtils.isEmpty(city) && TextUtils.isEmpty(outCity))
            filterCityDialog.setCityValue(cityType, tv_city.text.toString())
            filterCityDialog.show(childFragmentManager, "fcd")
            filterCityDialog.setDialogListener { p, s ->
                if (p == 1 || p == 0) {
                    city = s
                    outCity = null
                } else if (p == 2) {
                    city = null
                    outCity = s
                } else if (p == -2) {//取消选择
                    city = null
                    outCity = null
                }
                cityType = p
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
                val date = mDates[0]
                sysErr("--->$date")
                showDialog()
                sendDateRequest(date)
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
                if(SPUtils.instance().getBoolean(Const.User.IS_FIRST_SHOW_TIPS, true)){
                    tv_tip.visibility = View.VISIBLE
                }else{
                    tv_tip.visibility = View.GONE
                }
                tv_main_card_bg_im_id.gone()
                tv_main_card_Bg_tv_id.gone()
                fb_unlike.visible()
                btn_like.visible()
            }
        }
    }

    private fun sendDateRequest(dateBean: DateBean) {
        Request.dateUser(userId, dateBean.accountId).request(this, success = { msg, data ->
            val dateSendedDialog = DateSendedDialog()//35619 35641  35643    35589
            dateSendedDialog.arguments = bundleOf("data" to dateBean,"msg" to if(msg!=null)msg else "")
            dateSendedDialog.show(childFragmentManager, "d")
            mDates.remove(dateBean)
            mRecyclerView.adapter.notifyDataSetChanged()
            //请求下次
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
        startActivity<MyDateActivity>()
        tv_tip.visibility = View.GONE
        SPUtils.instance().put(Const.User.IS_FIRST_SHOW_TIPS,false).apply()
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