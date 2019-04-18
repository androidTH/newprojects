package com.d6.android.app.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *     author : jinjiarui
 *     time   : 2018/09/19
 *     desc   :
 *     version:
 */
data class MyAppointment(@SerializedName("sId") var sId:String?=""): Serializable {//约会的id
    @SerializedName("sAppointmentSignupId") var sAppointmentSignupId=""  //报名约会数据的id
    @SerializedName("sDesc") var sDesc:String?=""
    @SerializedName("iUserid") var iUserid:Int?=-1//报名约会的用户id
    @SerializedName("sUserName") var sUserName:String?=""
    @SerializedName("sPicUrl") var sPicUrl:String?="" //报名约会的用户头像
    @SerializedName("iAppointType") var iAppointType:Int?=-1
    @SerializedName("iStatus") var iStatus:Int?=-1 //报名约会的状态（1、发起 2、同意 3、拒绝 4、主动取消 5、过期自动取消）
    @SerializedName("iAppointUserid") var iAppointUserid:Int?=-1 //约会发布人的id
    @SerializedName("sAppointUserName") var sAppointUserName:String?="" //约会发布人的名称
    @SerializedName("sAppointmentPicUrl") var sAppointmentPicUrl:String?=""//约会发布人的头像
    @SerializedName("dAppointmentSignupCreatetime") var dAppointmentSignupCreatetime:Long = 0//报名约会时间
    @SerializedName("dAppointmentSignupUpdatetime") var dAppointmentSignupUpdatetime:Long=0//报名约会主动取消、对方同意、对方拒绝、过期自动取消的时间
    @SerializedName("iIsread") var iIsread:Int?=-1 //约会消息是否已读（1、未读  2、已读 ）
    @SerializedName("sPlace") var sPlace:String?="" //约会地点
    @SerializedName("sAppointPic") var sAppointPic:String?="" //"sAppointPic":"http://p22l7xdxa.bkt.clouddn.com/Fkqkg2-Wy4Cq2nAlthV92dCW9_o0,http://p22l7xdxa.bkt.clouddn.com/1535596704971.jpg,http://p22l7xdxa.bkt.clouddn.com/1536127172202.jpg,http://p22l7xdxa.bkt.clouddn.com/FrBTMocJHtq5ye0D6TxxE0Yo-Y-v,http://p22l7xdxa.bkt.clouddn.com/FhqGXx5hjTNHDttbWX4hFU2382-f"//约会图片描述
    @SerializedName("sSourceAppointPic") var sSourceAppointPic:String?="" //"sAppointPic":"http://p22l7xdxa.bkt.clouddn.com/Fkqkg2-Wy4Cq2nAlthV92dCW9_o0,http://p22l7xdxa.bkt.clouddn.com/1535596704971.jpg,http://p22l7xdxa.bkt.clouddn.com/1536127172202.jpg,http://p22l7xdxa.bkt.clouddn.com/FrBTMocJHtq5ye0D6TxxE0Yo-Y-v,http://p22l7xdxa.bkt.clouddn.com/FhqGXx5hjTNHDttbWX4hFU2382-f"//约会图片描述
    @SerializedName("sRefuseDesc") var sRefuseDesc:String?="" //sRefuseDesc:null,//约会拒绝原因（暂未使用）
    @SerializedName("dCreatetime") var dCreatetime:Long = 0 //"dCreatetime":1539418366000,//约会发布时间
    @SerializedName("dStarttime") var dStarttime:Long = 0// "dStarttime":1539078078000,//约会开始时间
    @SerializedName("dEndtime") var dEndtime:Long =0  //"dEndtime":1539855678000,//约会截至时间
    @SerializedName("dUpdatetime") var dUpdatetime:Long = 0 //"dUpdatetime":null//约会更新时间
    @SerializedName("iSex") var iSex:Int?=-1
    @SerializedName("iAge") var iAge:Int?=-1
    @SerializedName("iHeight") var iHeight:Int?=-1
    @SerializedName("iWeight") var iWeight:Int?=-1
    var iPoint:Int?=-1
//    @SerializedName("sSourceAppointPic")
//    var sSourceAppointPic:String?=""
}