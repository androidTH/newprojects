package com.d6.android.app.net

import com.d6.android.app.models.*
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import io.reactivex.Flowable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created on 2017/12/27.
 */
interface ApiServices {
    @POST("backstage/login/system_login")
    fun login(@Query("phone") phone: String?, @Query("loginName") account: String?, @Query("password") pwd: String, @Query("logintype") loginType: Int): Flowable<Response<UserData>>

    @POST("backstage/account/add")
    fun register(@Query("phone") phone: String, @Query("password") pwd: String, @Query("vercode") code: String, @Query("guoneiguowai") phoneType: String,@Query("sex")sex:Int): Flowable<Response<JsonObject>>

    @POST("backstage/login/getVerifyCode")
    fun sendSMSCode(@Query("phone") phone: String, @Query("vercodetype") vercodetype: Int, @Query("guoneiguowai") guoneiguowai: Int): Flowable<Response<JsonObject>>

    @POST("backstage/account/resetPwd")
    fun resetPwd(@Query("phone") phone: String, @Query("password") password: String, @Query("logintype") logintype: String = "1"): Flowable<Response<JsonObject>>

    @POST("backstage/account/resetPwdFirstStep")
    fun resetPwdFirstStep(@Query("phone") phone: String, @Query("vercode") vercode: String, @Query("logintype") logintype: Int = 1): Flowable<Response<JsonObject>>

    @POST("backstage/account/find")
    fun getUserInfo(@Query("userid") accountId: String): Flowable<Response<UserData>>

    @POST("backstage/banner/findByPage")
    fun getBanners(@Query("pageNum") pageNum: Int = 1,@Query("pageSize") pageSize: Int = 10,@Query("bannerkey") bannerkey: String="home"): Flowable<Response<Page<Banner>>>
    @POST("backstage/pieces/find")
    fun getInfo(@Query("piecesMark") piecesMark: String = "1"): Flowable<Response<JsonObject>>

    @POST("backstage/account/update")
    fun updateUserInfo(@Body userData: UserData): Flowable<Response<JsonObject>>

    @POST("backstage/squareclasses/find")
    fun getSquareTags(): Flowable<Response<ArrayList<SquareTag>>>

    @POST("backstage/square/del")
    fun deleteSquare(@Query("userid") userid:String,@Query("ids") ids:String?): Flowable<Response<JsonObject>>

    @POST("backstage/imessage/del")
    fun deleteSysMsg(@Query("userid") userid:String,@Query("ids") ids:String?): Flowable<Response<JsonObject>>

    @POST("backstage/square/findByPage")
    fun getSquareList(@Query("userid") accountId: String, @Query("classesid") classesid: String?, @Query("pageNum") pageNum: Int, @Query("pageSize") pageSize: Int = Request.PAGE_SIZE, @Query("limit") limit: Int = 0, @Query("sex") sex: Int = 2): Flowable<Response<Page<Square>>>

    @POST("backstage/square/find")
    fun getSquareDetail(@Query("userid") userId: String, @Query("ids") ids: String?, @Query("limit") limit: Int = Request.PAGE_SIZE): Flowable<Response<Square>>

    @POST("backstage/comments/findByPage")
    fun getCommentList(@Query("newsId") newsId: String, @Query("pageNum") pageNum: Int, @Query("pageSize") pageSize: Int = Request.PAGE_SIZE): Flowable<Response<Page<Comment>>>

    @POST("backstage/opinions/add")
    fun feedback(@Query("userid") accountId: String, @Query("content") content: String): Flowable<Response<JsonObject>>

    @POST("backstage/comments/add")
    fun addComment(@Query("userid") accountId: String, @Query("newsId") newsId: String, @Query("content") content: String, @Query("replyuserid") replyuserid: String?): Flowable<Response<JsonObject>>

    @POST("backstage/tool/webuploader/getqiniutoken")
    fun getQiniuToken(): Flowable<Response<JsonPrimitive>>

    @POST("backstage/speedabout/findByPage")
    fun getSpeedDateList(@Query("speedtype") speedType: Int, @Query("pageNum") pageNum: Int, @Query("beginTime") beginTime: String? = null, @Query("endTime") endTime: String? = null
                         , @Query("classesid") classesid: String? = null, @Query("arrayspeedstate") arraySpeedState: String? = null, @Query("speedwhere") area: String? = null
                         , @Query("handspeedwhere") guowaiarea: String? = null, @Query("arrayuserclassesid") arrayUserClassesId: String? = null, @Query("speedhomepage") speedhomepage: String? = null
                         , @Query("pageSize") pageSize: Int = Request.PAGE_SIZE): Flowable<Response<Page<MyDate>>>

    @POST("backstage/speedabout/find")
    fun getSpeedDetail(@Query("ids") ids: String?):Flowable<Response<MyDate>>

    @POST("backstage/lookabout/findByPage")
    fun getFindDateList(@Query("looktype") looktype: Int, @Query("pageNum") pageNum: Int, @Query("beginTime") beginTime: String? = null, @Query("endTime") endTime: String? = null
                        , @Query("classesid") classesid: String? = null, @Query("arraylookstate") arrayLookState: String? = null, @Query("userlookwhere") guoneiarea: String? = null
                        , @Query("userhandlookwhere") guowaiarea: String? = null, @Query("arrayuserclassesid") arrayUserClassesId: String? = null
                        , @Query("pageSize") pageSize: Int = Request.PAGE_SIZE): Flowable<Response<Page<MyDate>>>

    @POST("backstage/wxkf/find")
    fun searchWeChatId(@Query("kfName") kfName: String): Flowable<Response<JsonObject>>

    @POST("backstage/square/findByPageMySquare")
    fun getMySquares(@Query("userid") userid: String, @Query("limit") limit: Int, @Query("pageNum") pageNum: Int, @Query("pageSize") pageSize: Int = Request.PAGE_SIZE): Flowable<Response<Page<Square>>>

    @POST("backstage/upvote/add")
    fun addPraise(@Query("userid") userid: String, @Query("newsId") newsId: String?): Flowable<Response<JsonObject>>

    @POST("backstage/upvote/del")
    fun cancelPraise(@Query("userid") userid: String, @Query("newsId") newsId: String?): Flowable<Response<JsonObject>>

    @POST("backstage/selfabout/findByPage")
    fun getSelfReleaseList(@Query("pageNum") pageNum: Int, @Query("beginTime") beginTime: String? = null, @Query("endTime") endTime: String? = null
                           , @Query("guoneiarea") guoneiarea: String? = null, @Query("guowaiarea") guowaiarea: String? = null, @Query("arrayuserclassesid") arrayUserClassesId: String? = null, @Query("pageSize") pageSize: Int = Request.PAGE_SIZE): Flowable<Response<Page<MyDate>>>

    @POST("backstage/square/add")
    fun releaseSquare(@Query("userid") userid: String, @Query("classesid") classesid: String?, @Query("squarecity") city: String?, @Query("coverurl") coverurl: String?, @Query("content") content: String): Flowable<Response<JsonObject>>

    @POST("backstage/selfabout/add")
    fun releaseSelfAbout(@Query("userid") userid: String, @Query("content") content: String?, @Query("handlookwhere") handlookwhere: String?
                         , @Query("lookwhere") lookwhere: String?, @Query("city") city: String?, @Query("beginTime") beginTime: String?
                         , @Query("endTime") endTime: String?, @Query("selfpicurl") selfpicurl: String?): Flowable<Response<JsonObject>>

    @POST("backstage/userclasses/findauto")
    fun getUserLevels(): Flowable<Response<ArrayList<UserLevel>>>

    @POST("backstage/sysDict/findauto")
    fun getCities(@Query("paramKey") paramKey: String): Flowable<Response<ArrayList<City>>>

    @POST("backstage/comments/findByPageguangchangxiaoxi")
    fun getSquareMessages(@Query("userid") userid: String, @Query("pageNum") pageNum: Int, @Query("createTime") createTime: String? = null, @Query("pageSize") pageSize: Int = Request.PAGE_SIZE): Flowable<Response<Page<SquareMessage>>>

    @POST("backstage/rongcloud/gettalkdetails")
    fun getTalkDetails(@Query("fromuserid") fromuserid: String, @Query("touserid") touserid: String, @Query("checkdate") checkdate: String): Flowable<Response<JsonObject>>

    @POST("backstage/imessage/findByPage")
    fun getSystemMsgs(@Query("userid") userid: String, @Query("pageNum") pageNum: Int, @Query("createTime") createTime: String? = "", @Query("pageSize") pageSize: Int = Request.PAGE_SIZE): Flowable<Response<Page<SysMessage>>>

    @POST("backstage/account/updateumengdevicetype")
    fun updateDeviceType(@Query("userid") userid: String, @Query("devicetype") devicetype:String = "android"): Flowable<Response<JsonObject>>

    @POST("backstage/new_login/getVerifyCode")
    fun getVerifyCodeV2(@Query("phone") phone: String, @Query("vercodetype") vercodetype:Int): Flowable<Response<JsonObject>>

    @POST("backstage/new_login/system_login")
    fun loginV2(@Query("logintype") logintype: Int, @Query("vercode") vercode:String?,@Query("phone") phone:String?=null, @Query("guoneiguowai") guoneiguowai:String?=null, @Query("openid") openid:String?=null): Flowable<Response<UserData>>

    @POST("backstage/dataDict/find")
    fun findDataDict(@Query("dataKey") dataKey:String?="quhao"): Flowable<Response<JsonPrimitive>>

    @POST("backstage/black/add")
    fun addBlackList(@Query("userid") userid:String,@Query("blackuserid") blackuserid:String): Flowable<Response<JsonPrimitive>>

    @POST("backstage/tip/add")
    fun report(@Query("userid") userid:String,@Query("tipuserid") tipuserid:String,@Query("content") content:String,@Query("tiptype") tiptype:String): Flowable<Response<JsonPrimitive>>

    @POST("backstage/engagementsaccount/update")
    fun updateSeeCount(@Query("userid") userid:String,@Query("seecount") seecount:String="1"): Flowable<Response<JsonPrimitive>>

    @POST("backstage/engagementsaccount/findseecount")
    fun findSeeCount(@Query("userid") userid:String): Flowable<Response<JsonPrimitive>>

    @POST("backstage/engagements/leiji")
    fun myDateCount(@Query("userid") userid:String): Flowable<Response<JsonPrimitive>>

    @POST("backstage/engagements/leiji")
    fun dateMeCount(@Query("engagementuserid") userid:String): Flowable<Response<JsonPrimitive>>

    @POST("backstage/engagements/sasamieru")
    fun getDateSuccessCount(): Flowable<Response<JsonPrimitive>>

    @POST("backstage/engagementsaccount/update")
    fun updateDateInfo(@Query("userid") userid:String,@Query("egagementtype")egagementtype:String?,@Query("egagementtext")egagementtext:String?,@Query("userhandlookwhere")userhandlookwhere:String?,@Query("userlookwhere")userlookwhere:String?,@Query("phone")phone:String?,@Query("egagementwx")egagementwx:String?,@Query("openEgagementflag")openEgagementflag:String?): Flowable<Response<JsonPrimitive>>

    @POST("backstage/engagements/update")
    fun updateDateState(@Query("ids")ids:String,@Query("state")state:String): Flowable<Response<JsonPrimitive>>

    @POST("backstage/engagements/add")
    fun dateUser(@Query("userid")userid:String,@Query("engagementuserid")engagementuserid:String): Flowable<Response<JsonObject>>

    @POST("backstage/engagementsaccount/findByPagewoyuebieren")
    fun findMyDatingList(@Query("userid")userid:String,@Query("pageNum")pageNum:Int,@Query("pageSize")pageSize:Int=Request.PAGE_SIZE): Flowable<Response<Page<NewDate>>>

    @POST("backstage/engagementsaccount/findByPage")
    fun findDatingMeList(@Query("userid")userid:String,@Query("pageNum")pageNum:Int,@Query("pageSize")pageSize:Int=Request.PAGE_SIZE): Flowable<Response<Page<NewDate>>>

    @POST("backstage/engagementsaccount/findcart")
    fun getHomeDateList(@Query("userid")userid:String,@Query("sex")sex:String,@Query("egagementtype")egagementtype:Int,@Query("userlookwhere")userlookwhere:String?,@Query("userhandlookwhere")userhandlookwhere:String?): Flowable<Response<ArrayList<DateBean>>>

    @POST("backstage/engagementsaccount/percent")
    fun getAuthState(@Query("userid")userid:String): Flowable<Response<JsonObject>>

}