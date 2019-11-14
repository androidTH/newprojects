package com.d6.android.app.activities

import android.Manifest
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.text.TextUtils
import com.amap.api.location.AMapLocationClient
import com.d6.android.app.R
import com.d6.android.app.adapters.HomeVoicePageAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.SelectedSexPopup
import com.d6.android.app.fragments.VoiceChatFragment
import com.d6.android.app.models.DateType
import com.d6.android.app.models.Province
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.User.ISNOTLOCATION
import com.d6.android.app.utils.Const.User.USER_ADDRESS
import com.d6.android.app.utils.Const.User.USER_PROVINCE
import com.d6.android.app.widget.diskcache.DiskFileUtils
import com.facebook.drawee.backends.pipeline.Fresco
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_audio_chat.*
import org.jetbrains.anko.startActivityForResult

/**
 * 连麦
 */
class AudioChatActivity : BaseActivity() {

    private var city: String = ""

    var province = Province(Const.LOCATIONCITYCODE,"不限/定位")

    private var pageSelected = 0

    private val lastTime by lazy{
        SPUtils.instance().getString(Const.LASTTIMEOFPROVINCEINFIND)
    }

    private val cityJson by lazy{
        DiskFileUtils.getDiskLruCacheHelper(this).getAsString(Const.PROVINCE_DATAOFFIND)
    }

    private var userJson = SPUtils.instance().getString(Const.USERINFO)
    private val mSelfDateTypes = ArrayList<DateType>()
    lateinit var mPopupSex:SelectedSexPopup
    fun IsNotNullPopupSex()=::mPopupSex.isInitialized

    private var mSelectedSex = -1
    private var from = "SquareFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_chat)
        immersionBar.init()

        from = intent.getStringExtra("from")

        if (TextUtils.equals(from, "SquareFragment")) {
            if (TextUtils.equals("0", getUserSex())) {
                tv_audio_sex.text = "男生"
                mSelectedSex = 1
            } else {
                tv_audio_sex.text = "女生"
                mSelectedSex = 0
            }
        } else if (TextUtils.equals(from, "AudioChatActivity")) {
            tv_audio_sex.text = getString(R.string.string_sex)
            mSelectedSex = -1
        }

        var mFragments = listOf(
                VoiceChatFragment.instance("", mSelectedSex)
        )

        iv_back_close.setOnClickListener {
            finish()
        }

        tv_add_voicechat.setOnClickListener {
            isCheckOnLineAuthUser(this, getLocalUserId()){
                startActivityForResult<VoiceChatCreateActivity>(10)
            }
        }

        tv_audio_sex.setOnClickListener {
            if(IsNotNullPopupSex()){
                showSex()
            }else{
                mPopupSex = SelectedSexPopup.create(this@AudioChatActivity)
                        .setDimView(viewpager_appointment)
                        .apply()
                showSex()
            }
        }

        viewpager_appointment.adapter = HomeVoicePageAdapter(supportFragmentManager,mFragments,mSelfDateTypes)
        viewpager_appointment.offscreenPageLimit = mFragments.size
        viewpager_appointment.addOnPageChangeListener(object:ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                pageSelected = position
            }
        })

        checkLocation()

        viewpager_appointment.postDelayed(object:Runnable{
            override fun run() {
                mPopupSex = SelectedSexPopup.create(this@AudioChatActivity)
                        .setDimView(viewpager_appointment)
                        .apply()
            }
        },200)
    }

    private fun showSex() {
        mPopupSex.showAsDropDown(rl_audio_top, 0,0)
        mPopupSex.setOnPopupItemClick { basePopup, position, string ->
            mSelectedSex = position
            if (mSelectedSex == -1) {
                tv_audio_sex.text = getString(R.string.string_sex)
            }else{
                tv_audio_sex.text = string
            }
            getFragment()
        }

        mPopupSex.setOnDismissListener {

        }
    }

    private fun getFragment(){
        var mVoiceChatFragment:VoiceChatFragment = supportFragmentManager.fragments[pageSelected] as VoiceChatFragment
        mVoiceChatFragment?.let {
            it.refresh(mSelectedSex)
        }
    }


    private val locationClient by lazy {
        AMapLocationClient(this)
    }

    private fun startLocation() {
        locationClient.stopLocation()
        locationClient.startLocation()
    }


    private fun checkLocation(){
        RxPermissions(this).request(Manifest.permission.ACCESS_COARSE_LOCATION).subscribe {
            if (it) {
                startLocation()
                SPUtils.instance().put(ISNOTLOCATION,false).apply()
            }else{
                SPUtils.instance().put(ISNOTLOCATION,true).apply()
            }
        }

        locationClient.setLocationListener {
            if (it != null) {
                locationClient.stopLocation()
                SPUtils.instance().put(USER_ADDRESS,it.city).apply() //it.city
                SPUtils.instance().put(USER_PROVINCE,it.province).apply()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Fresco.getImagePipeline().clearMemoryCaches()
        immersionBar.destroy()
    }
}
