package com.d6.android.app.utils

import com.d6.android.app.R
import com.d6.android.app.net.API

/**
 *
 */
object Const {
//
    object User{
        val IS_LOGIN="isLogin"
        val IS_FIRST="isFirst"
        val IS_FIRST_SHOW_TIPS="isFirstShowTips"
        val USER_ID="userId"
        val USER_SEX="userSex"
        val USER_PHONE="userPhone"
        val USER_CLASS_ID="userClassID"
        val USER_TOKEN="userToken"
        val USER_HEAD="userHead"
        val USER_NICK="userNick"
        val RONG_TOKEN ="rongToken"
        val USER_ADDRESS = "address"
        val USER_PROVINCE = "province"
        val USER_SCREENID = "screenID"
        val USER_DATACOMPLETION = "iDatacompletion"
        val USER_MESSAGESETTING = "messageSetting"
        //别人约我
        var SOMEONE_ELSE_MAKES_AN_APPOINTMENT_WITH_ME="someoneElseMakesAnAppointmentWithMe"
        var IASKSOMEONEELSE="IAskSomeoneElse"  //我约的人
        var  DEVICETOKEN ="devicetoken"
        var  HEADERIMAGE ="headerimage"
        var  SELECTIMAGE ="selectimage"
        var  USERPOINTS_NUMS = "UserPointNums"
}

    val SQUAREMSG_LAST_TIME = "Square_lastTime" //广场消息时间
    val SYSMSG_LAST_TIME = "SysMsylastTime"//系统消息时间
    val NEW_MESSAGE = "com.d6.app.new_msg"
    val BUGTAGS_KEY = "e3ed18af47d9993fbfbc5dc02194079e"
    val UMENG_APPKEY = "5a5b309af29d9835ae000262"
    val UMENG_MESSAGE_SECRET = "0f16af7d3011a5aad7cf82a996b6b94c"
    val XIAOMIAPPID="2882303761517748078"
    val XIAOMIAPPKEY = "5141774885078"
    val SERVICE_WECHAT_CODE = "service_wechat_code"
    val SCORE_EXPLAIN_CODE = "integral_explain"

    // APP_ID 替换为你的应用从官方网站申请到的合法appId
    @JvmField
    var WXPAY_APP_ID:String?= "wx43d13a711f68131c"

    @JvmField
    var dateTypes = arrayOf("旅行","吃饭","电影","喝酒","不限")
    @JvmField
    var dateTypesImg = arrayOf(R.mipmap.invitation_travel_small,R.mipmap.invitation_meal_small,R.mipmap.invitation_film_small,
            R.mipmap.invitation_drink_small,R.mipmap.invitation_nolimit_small)
    //正式 f509c00b16c12f2d7c3306d3383e7655 测试 f509c00b16c12f2d7c3306d3383e7655

    var dateTypesDefault = arrayOf(R.mipmap.invitation_travel_default,R.mipmap.invitation_meal_default,R.mipmap.invitation_film_default,
            R.mipmap.invitation_drink_default,R.mipmap.invitation_nolimit_default)

    var dateTypesSelected = arrayOf(R.mipmap.invitation_travel_seleted,R.mipmap.invitation_meal_seleted,R.mipmap.invitation_film_seleted,
            R.mipmap.invitation_drink_seleted,R.mipmap.invitation_nolimit_seleted)

    var dateTypesBig = arrayOf(R.mipmap.invitation_travel_feed,R.mipmap.invitation_meal_feed,R.mipmap.invitation_film_feed,
            R.mipmap.drink_nolimit_feed,R.mipmap.invitation_nolimit_feed)//invitation_shopping_feed


    val FROM_MY_DATELIST = "MyDateListActivity"
    val FROM_MY_DATESUCCESS = "OpenDateSuccessDialog"
    @JvmField
    val FROM_MY_CHATDATE = "OpenDateSuccessChat"

    @JvmField
    var selectCategoryType:String=""
    @JvmField
    var LOCATIONSUCCESS = "location_success"
    var LOCATIONFAIL = "location_fail"
    var PROVINCE_DATA = "province" //省份
    var PROVINCE_DATAOFFIND = "provinceOfFind" //省份
    var LASTLONGTIMEOFProvince = "LastLoginTimeOfProvince"//查询省份的时间设置
    var LASTTIMEOFPROVINCEINFIND = "LastTimeOfProvinceInFind"//发现中的查询省份时间
    var IGNORE_VERSION ="ignore_version" //忽略版本
    var LASTDAYTIME = "LastDayTime"
    var NO_LIMIT_ERA = "不限/定位不限地区"
    @JvmField
    var LOCATIONCITYCODE="100010"

    @JvmField
    val DOUPDATEUSERINFOCODE = 1000

    val USERINFO = "UserInfo"

    val CustomerServiceId="5"
    val CustomerServiceWomenId="98314"

    val PRIVATECHAT_APPLY = "com.d6.app.privatechat_apply_msg"

    @JvmField
    val IS_FIRST_SHOW_TIPS = "is_first_show_tips"

    @JvmField
    val YOUMENG_MSG_NOTIFION = "com.d6.app.youmeng_msg_notifion"

    @JvmField
    val Pic_Size_wh400 = "?imageView2/0/w/400/h/400"
    @JvmField
    val Pic_Size_wh300 = "?imageView2/0/w/300/h/300"
    @JvmField
    val Pic_Size_wh500 = "?imageView2/0/w/500/h/500"

    @JvmField
    val Pic_Thumbnail_Size_wh200  = "?imageMogr2/auto-orient/thumbnail/200x200/quality/100"
    @JvmField
    val Pic_Thumbnail_Size_wh300  = "?imageMogr2/auto-orient/thumbnail/300x300/quality/100"
    @JvmField
    val Pic_Thumbnail_Size_wh400  = "?imageMogr2/auto-orient/thumbnail/400x400/quality/100"
    @JvmField
    val Pic_Thumbnail_Size_wh600  = "?imageMogr2/auto-orient/thumbnail/600x600/quality/100"
    val Pic_Thumbnail_Size_wh800  = "?imageMogr2/auto-orient/thumbnail/800x800/quality/100"

    val UpdateAppUrl = API.BASE_URL+"backstage/version/getByVersion"

    @JvmField
    val PUSH_ISNOTSHOW="push_isnotshow"
    @JvmField
    var CHOOSE_Friends="choosefriends"
    @JvmField
    var DIALOG_SHOW_MAX=9
}