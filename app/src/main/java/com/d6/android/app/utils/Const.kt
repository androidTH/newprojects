package com.d6.android.app.utils

import com.d6.android.app.R

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
        //别人约我
        var SOMEONE_ELSE_MAKES_AN_APPOINTMENT_WITH_ME="someoneElseMakesAnAppointmentWithMe"
        var IASKSOMEONEELSE="IAskSomeoneElse"  //我约的人
        var  DEVICETOKEN ="devicetoken"
        var  HEADERIMAGE ="headerimage"
        var  SELECTIMAGE ="selectimage"
}

    val LAST_TIME = "lastTime"
    val NEW_MESSAGE = "com.d6.app.new_msg"
    val BUGTAGS_KEY = "e3ed18af47d9993fbfbc5dc02194079e"
    val UMENG_APPKEY = "5a5b309af29d9835ae000262"
    val UMENG_MESSAGE_SECRET = "0f16af7d3011a5aad7cf82a996b6b94c"
    val SERVICE_WECHAT_CODE = "service_wechat_code"
    // APP_ID 替换为你的应用从官方网站申请到的合法appId
    @JvmField
    var WXPAY_APP_ID:String?= "wxd930ea5d5a258f4f"

    var dateTypes = arrayOf("旅行","吃饭","电影","唱歌","运动","征婚","其它")
    var dateTypesImg = arrayOf(R.mipmap.invitation_travel_small,R.mipmap.invitation_meal_small,R.mipmap.invitation_film_small,
            R.mipmap.invitation_shopping_small,R.mipmap.invitation_nolimit_small)
    //正式 f509c00b16c12f2d7c3306d3383e7655 测试 f509c00b16c12f2d7c3306d3383e7655
}