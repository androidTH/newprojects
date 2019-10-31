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
        val IS_FIRST_SHOW_RGDIALOG="isFirstShowRGDialog"
        val IS_FIRST_SHOW_SELFDATEDIALOG="isFirstShowSelfDateDialog"
        val IS_FIRST_SHOW_FINDDIALOG="isFirstShowFindDialog"
        val IS_FIRST_SHOW_FINDNOTICEDIALOG="isFirstShowFindNoticDialog"
        val IS_FIRST_SHOW_FINDLASTDAYNOTICEDIALOG="isFirstShowFindLastDayNoticDialog"
        val IS_FIRST_FAST_CLICK="isFirstFastClick"
        @JvmField
        val USER_ID="userId"
        @JvmField
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
        var  USERPOINTS_NUMS = "UserPointNums"//用户积分数量
        var  USERLOVE_NUMS = "UserLoveNums"//用户爱心数量
        var  SLOGINTOKEN ="sLoginToken"
}

    var WEIXINID = "wx43d13a711f68131c"
    var WEIXINSECERT= "00537b54033cd022ceda1894bae5ebf5"
    val SQUAREMSG_LAST_TIME = "Square_lastTime" //广场消息时间
    val SYSMSG_LAST_TIME = "SysMsylastTime"//系统消息时间
    val NEW_MESSAGE = "com.d6.app.new_msg"
    val CHAT_MESSAGE = "com.d6.app.chat_msg_count"
    val MINE_MESSAGE = "com.d6.app.mine_msg"//我的关注 粉丝提醒
    val MINE_MANSERVICE_YOUKE = "com.d6.app.mine_manservice_youke"//我的关注 粉丝提醒
    val HOMEDATE_STATEBAR = "com.d6.app.homedate_statebar"//约会状态栏
    val BUGTAGS_KEY = "e3ed18af47d9993fbfbc5dc02194079e"
    val UMENG_APPKEY = "5a5b309af29d9835ae000262"
    val UMENG_MESSAGE_SECRET = "0f16af7d3011a5aad7cf82a996b6b94c"
    val XIAOMIAPPID="2882303761517748078"
    val XIAOMIAPPKEY = "5141774885078"
    val SERVICE_WECHAT_CODE = "service_wechat_code"
    val SCORE_EXPLAIN_CODE = "integral_explain"
    val PIECES_VIP_INSTRODUCE = "vip_introduce"
    val CONST_RES_MIPMAP= "res:///"

    // APP_ID 替换为你的应用从官方网站申请到的合法appId
    @JvmField
    var WXPAY_APP_ID:String?= "wx43d13a711f68131c"

    @JvmField
    var dateTypes = arrayOf("旅行","吃饭","电影","看电影","不限","聊天","游戏","健身") // 游戏

    @JvmField
    var dateTypesImg = arrayOf(R.mipmap.invitation_travel_small,R.mipmap.invitation_meal_small,R.mipmap.invitation_film_small,
            R.mipmap.invitation_drink_small,R.mipmap.invitation_nolimit_small,R.mipmap.invitation_chat_small,R.mipmap.invitation_game_small,
            R.mipmap.invitation_fitness_small)
    //正式 f509c00b16c12f2d7c3306d3383e7655 测试 f509c00b16c12f2d7c3306d3383e7655

    var dateTypesDefault = arrayOf(R.mipmap.invitation_travel_default,R.mipmap.invitation_meal_default,R.mipmap.invitation_film_default,
            R.mipmap.invitation_drink_default,R.mipmap.invitation_nolimit_default,R.mipmap.invitation_chat_default,R.mipmap.invitation_game_default,
            R.mipmap.invitation_fitness_default)

    var dateTypesSelected = arrayOf(R.mipmap.invitation_travel_seleted,R.mipmap.invitation_meal_seleted,R.mipmap.invitation_film_seleted,
            R.mipmap.invitation_drink_seleted,R.mipmap.invitation_nolimit_seleted,R.mipmap.invitation_chat_seleted,R.mipmap.invitation_game_seleted
            ,R.mipmap.fitness_seleted)

    var dateTypesBig = arrayOf(R.mipmap.invitation_travel_feed,R.mipmap.invitation_meal_feed,R.mipmap.invitation_film_feed,
            R.mipmap.drink_nolimit_feed,R.mipmap.invitation_nolimit_feed,R.mipmap.chat_nolimit_feed,R.mipmap.game_nolimit_feed
            ,R.mipmap.game_fitness_feed)//invitation_shopping_feed

    var dateListTypes = arrayOf(R.mipmap.invitation_trip_white,R.mipmap.invitation_meal_white,R.mipmap.invitation_film_white,
            R.mipmap.details_drink_icon_white,R.mipmap.details_nolimit_icon_white,R.mipmap.invitation_chat_white,R.mipmap.invitation_game_white
            ,R.mipmap.invitation_fitness_white)

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
    var PROVINCE_DATAOFMEMBER = "provinceOfMember" //会员
    var LASTLONGTIMEOFProvince = "LastLoginTimeOfProvince"//查询省份的时间设置
    var LASTTIMEOFPROVINCEINFIND = "LastTimeOfProvinceInFind"//发现中的查询省份时间
    var LASTTIMEOFPROVINCEINMEMBER = "LastTimeOfProvinceInMember"//发现中的查询省份时间
    var IGNORE_VERSION ="ignore_version" //忽略版本
    var LASTDAYTIME = "LastDayTime"
    var NO_LIMIT_ERA = "不限/定位不限地区"
    @JvmField
    var LOCATIONCITYCODE="100010"

    @JvmField
    val DOUPDATEUSERINFOCODE = 1000

    val USERINFO = "UserInfo"

    @JvmField
    val CustomerServiceId="5"
    @JvmField
    val CustomerServiceWomenId="98314"

    val PRIVATECHAT_APPLY = "com.d6.app.privatechat_apply_msg"

    @JvmField
    val IS_FIRST_SHOW_TIPS = "is_first_show_tips"

    @JvmField
    val IS_FIRST_SHOWUNKNOW_TIPS = "is_first_show_unknow_tips"

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

//    val UpdateAppUrl = API.BASE_URL+"backstage/version/getByVersion"
    val UpdateAppUrl_PiecesMark = API.BASE_URL+"backstage/pieces/find"

    @JvmField
    val PUSH_ISNOTSHOW="push_isnotshow"
    @JvmField
    var CHOOSE_Friends="choosefriends"

    @JvmField
    var CHOOSE_TOPIC="choosetopic"
    @JvmField
    var USERINFO_PERCENT = "userinfo_percent"
    @JvmField
    var DIALOG_SHOW_MAX=9

    @JvmField
    var NO_VIP_FROM_TYPE = "No_Vip_From"

    @JvmField
    var WOMEN_HEADERURL = "http://p22l7xdxa.bkt.clouddn.com/1556507697336.jpg?imageMogr2/auto-orient/thumbnail/200x200/quality/100"
    @JvmField
    var MEN_HEADERURL = "http://p22l7xdxa.bkt.clouddn.com/1556507266810.jpg?imageMogr2/auto-orient/thumbnail/200x200/quality/100"

    @JvmField
    var OPENSTALL_CHANNEL = "openstall_channel"

    @JvmField
    var SEND_GROUP_TIPSMESSAGE = "send_group_tipsmessage"

    @JvmField
    var CHECK_OPEN_UNKNOW = "IsOpenUnKnow" //检查是否开启匿名
    @JvmField
    var CHECK_OPEN_UNKNOW_MSG = "IsOpenUnKnowMsg" //检查是否开启匿名提示信息

    @JvmField
    var WHO_ANONYMOUS = "whoanyonmous" //检查是否开启匿名提示信息

    @JvmField
    var GROUPSPLIT_LEN = 3 //群组分割长度

    @JvmField
    var DEBUG_MODE = "debug_mode"

    @JvmField
    var ISUPDOWN = "IsUpDown"


    @JvmField
    var SEND_FIRST_PRIVATE_TIPSMESSAGE = "send_first_private_tipsmessage"//第一次给某个用户发消息时
    @JvmField
    var RECEIVER_FIRST_PRIVATE_TIPSMESSAGE = "receiver_first_private_tipsmessage"//第一次收到某个用户发消息时


    @JvmField
    var APPLAY_CONVERTION_ISTOP = "applay_convertion_istop"

    @JvmField
    var CONVERSATION_APPLAY_DATE_TYPE = "conversation_applay_date_type" //申请约会

    @JvmField
    var CONVERSATION_APPLAY_PRIVATE_TYPE = "conversation_applay_private_type" //申请私聊

    @JvmField
    var CHAT_TARGET_ID = ""

    @JvmField
    var INSTALL_DATA01 = "install_data01"

    @JvmField
    var INSTALL_DATA02 = "install_data02"

    @JvmField
    var LOGIN_FOR_POINT_NEW="loginForPointNew"

    @JvmField
    var SENDLOVEHEART_DIALOG ="sendLoveHeartDialog"//来自于送爱心dialog

}