package com.d6.android.app.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import me.yokeyword.indexablerv.IndexableEntity

/**
 * Created on 2018/1/15.
 */
data class IntegralExplain(var iAppointPoint: String?): Parcelable {
     var iAppointPointRefuse:String=""
     var iAppointPointCancel:String=""

     constructor(parcel: Parcel) : this(parcel.readString()) {
          iAppointPointRefuse = parcel.readString()
          iAppointPointCancel = parcel.readString()
     }

     override fun writeToParcel(parcel: Parcel, flags: Int) {
          parcel.writeString(iAppointPoint)
          parcel.writeString(iAppointPointRefuse)
          parcel.writeString(iAppointPointCancel)
     }

     override fun describeContents(): Int {
          return 0
     }

     companion object CREATOR : Parcelable.Creator<IntegralExplain> {
          override fun createFromParcel(parcel: Parcel): IntegralExplain {
               return IntegralExplain(parcel)
          }

          override fun newArray(size: Int): Array<IntegralExplain?> {
               return arrayOfNulls(size)
          }
     }
}